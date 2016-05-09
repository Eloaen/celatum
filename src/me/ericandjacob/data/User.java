package me.ericandjacob.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import me.ericandjacob.util.CryptoUtils;
import me.ericandjacob.util.DataCounterOutputStream;
import me.ericandjacob.util.NumUtils;
import me.ericandjacob.util.StreamUtils;

public class User {

  private UserList userList;
  private AccountList accounts;
  private byte[] encryptedData;
  private int actualDataLength;
  private boolean isDecrypted;
  private byte[] salt;
  private byte[] iv;
  private byte[] usernameSalt;
  private byte[] usernameHash;

  protected User(UserList list) {
    this.userList = list;
    this.isDecrypted = true;
    this.accounts = new AccountList();
  }

  protected User(UserList list, InputStream userData, int length) throws IOException {
    this(list);
    InputStream usuableData = new ByteArrayInputStream(StreamUtils.readByteArray(userData, length));
    int usernameSaltLength = StreamUtils.readInt(usuableData);
    int usernameHashLength = StreamUtils.readInt(usuableData);
    int ivLength = StreamUtils.readInt(usuableData);
    int saltLength = StreamUtils.readInt(usuableData);
    int encryptedDataLength = StreamUtils.readInt(usuableData);
    this.actualDataLength = StreamUtils.readInt(usuableData);
    this.usernameSalt = StreamUtils.readByteArray(usuableData, usernameSaltLength);
    this.usernameHash = StreamUtils.readByteArray(usuableData, usernameHashLength);
    this.iv = StreamUtils.readByteArray(usuableData, ivLength);
    this.salt = StreamUtils.readByteArray(usuableData, saltLength);
    this.encryptedData = StreamUtils.readByteArray(usuableData, encryptedDataLength);
    this.isDecrypted = false;
  }

  /**
   * Sets the username to the new name. If it is present in the containing UserList, an
   * IllegalArgumentException will be thrown.
   * 
   * @param name
   */
  public void setUsername(String name) {
    if (this.userList != null) {
      if (this.userList.hasUsername(name)) {
        throw new IllegalArgumentException(
            "UserList containing this User already has a user with this username.");
      }
    }
    this.usernameSalt = CryptoUtils.generateSalt(10);
    try {
      this.usernameHash =
          CryptoUtils.generateHash("SHA-256", name.getBytes("UTF-8"), this.usernameSalt);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if the given username is the same as this User's username
   * 
   * @param name username to check
   * @return if the usernames are the same
   */
  public boolean checkUsername(String name) {
    if (this.usernameHash == null || this.usernameSalt == null) {
      return false;
    }
    try {
      byte[] hash = CryptoUtils.generateHash("SHA-256", name.getBytes("UTF-8"), this.usernameSalt);
      return CryptoUtils.isEqual(hash, this.usernameHash);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
      return false;
    }
  }

  // encryption from:
  // http://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption#992413
  // http://stackoverflow.com/questions/10341150/using-cipheroutputstream-aes-to-write-a-string-to-a-file

  /**
   * Encrypts the User with the given password.
   * 
   * @param password password to encrypt the user with
   * @throws IllegalArgumentException if the user is already encrypted
   */
  public Optional<Exception> encrypt(char[] password) {
    if (!this.isDecrypted) {
      throw new IllegalArgumentException("Already encrypted");
    }
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      this.salt = CryptoUtils.generateSalt(10);
      KeySpec spec = new PBEKeySpec(password, this.salt, 65536, 128);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secret);
      AlgorithmParameters params = cipher.getParameters();
      this.iv = params.getParameterSpec(IvParameterSpec.class).getIV();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      OutputStream os = new DataCounterOutputStream(new CipherOutputStream(bos, cipher));
      StreamUtils.writeInt(os, this.accounts.getLength());
      for (Account account : this.accounts.getAccountsCopy()) {
        byte[] site = account.getSite().getBytes(StandardCharsets.UTF_8);
        byte[] accPassword = CryptoUtils.toBytes(account.getPassword());
        byte[] username = account.getUsername().getBytes(StandardCharsets.UTF_8);
        StreamUtils.writeInt(os, site.length);
        StreamUtils.writeInt(os, accPassword.length);
        StreamUtils.writeInt(os, username.length);
        os.write(site);
        os.write(accPassword);
        os.write(username);
        CryptoUtils.clearByteArray(accPassword);
        this.accounts.removeAccount(account);
      }
      os.close();
      this.encryptedData = bos.toByteArray();
      this.actualDataLength = ((DataCounterOutputStream) os).getCount();
      this.accounts = null;
      this.isDecrypted = false;
    } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
        | InvalidKeyException | InvalidParameterSpecException | IOException e) {
      return Optional.of(e);
    }
    return Optional.empty();
  }

  /**
   * Decrypts the User using the given password.
   * 
   * @param password The password to decrypt the User with
   * @throws IllegalArgumentException if the user is already decrypted
   */
  public Optional<Exception> decrypt(char[] password) {
    if (this.isDecrypted) {
      throw new IllegalArgumentException("Already decrypted");
    }
    try {
      this.accounts = new AccountList();
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(password, this.salt, 65536, 128);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.iv));
      InputStream is = new CipherInputStream(new ByteArrayInputStream(this.encryptedData), cipher);
      int amountUsers = StreamUtils.readInt(is);
      for (int i = 0; i < amountUsers; ++i) {
        int siteLength = StreamUtils.readInt(is);
        int accPasswordLength = StreamUtils.readInt(is);
        int usernameLength = StreamUtils.readInt(is);
        String site = new String(StreamUtils.readByteArray(is, siteLength), StandardCharsets.UTF_8);
        char[] accPassword = CryptoUtils.toChars(StreamUtils.readByteArray(is, accPasswordLength));
        String username = new String(StreamUtils.readByteArray(is, usernameLength));
        this.accounts.addAccount(username, site, accPassword);
      }
      this.encryptedData = null;
      this.actualDataLength = -1;
      this.isDecrypted = true;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
        | InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {
      return Optional.of(e);
    }
    return Optional.empty();
  }

  /**
   * Encrypts the User with password, then writes User to out, then decrypts the user
   * 
   * @param password
   * @param out
   * @throws IllegalArgumentException if the user is encrypted
   */
  public void writeUserToStream(char[] password, OutputStream out) throws IOException {
    if (!this.isDecrypted) {
      throw new IllegalArgumentException("User is already encrypted");
    }
    this.encrypt(password);
    this.writeUserToStream(out);
    this.decrypt(password);
  }

  /**
   * Writes an already encrypted User to out
   * 
   * @param out
   * @throws IllegalArgumentException if the user is not encrypted
   */
  public void writeUserToStream(OutputStream out) throws IOException {
    if (this.isDecrypted) {
      throw new IllegalArgumentException("Must be encrypted");
    }
    int length = 0;
    length += this.usernameSalt.length + 4;
    length += this.usernameHash.length + 4;
    length += this.iv.length + 4;
    length += this.salt.length + 4;
    length += this.encryptedData.length + 4;
    length += 4; // this.actualDataLength
    byte[] buffer = new byte[length - this.encryptedData.length + 4];
    byte[][] arrays = new byte[][] {NumUtils.intToByteArray(length),
        NumUtils.intToByteArray(this.usernameSalt.length),
        NumUtils.intToByteArray(this.usernameHash.length), NumUtils.intToByteArray(this.iv.length),
        NumUtils.intToByteArray(this.salt.length),
        NumUtils.intToByteArray(this.encryptedData.length),
        NumUtils.intToByteArray(this.actualDataLength), this.usernameSalt, this.usernameHash,
        this.iv, this.salt};
    int offset = 0;
    for (int i = 0; i < arrays.length; ++i) {
      System.arraycopy(arrays[i], 0, buffer, offset, arrays[i].length);
      offset += arrays[i].length;
    }
    out.write(buffer);
    out.write(this.encryptedData);
  }

  public AccountList getAccounts() {
    return this.accounts;
  }

  public boolean isDecrypted() {
    return this.isDecrypted;
  }

  @Override
  public String toString() {
    StringBuilder builder =
        new StringBuilder().append("User[decrypted=").append(this.isDecrypted());
    if (this.isDecrypted()) {
      builder.append(",accountlist=").append(this.accounts);
    }
    return builder.append("]").toString();
  }

}
