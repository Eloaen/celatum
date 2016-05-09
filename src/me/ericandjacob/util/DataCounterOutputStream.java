package me.ericandjacob.util;

import java.io.IOException;
import java.io.OutputStream;

public class DataCounterOutputStream extends OutputStream {

  private OutputStream out;
  private int count;

  public DataCounterOutputStream(OutputStream out) {
    this.out = out;
    this.count = 0;
  }

  @Override
  public void write(int b) throws IOException {
    this.count++;
    this.out.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    this.count += len;
    this.out.write(b, off, len);
  }

  @Override
  public void write(byte[] b) throws IOException {
    this.count += b.length;
    this.out.write(b);
  }

  @Override
  public void flush() throws IOException {
    this.out.flush();
  }

  @Override
  public void close() throws IOException {
    this.out.close();
  }
  
  public int getCount() {
    return this.count;
  }
  
  @Override
  public String toString() {
    return new StringBuffer().append("DataCounterOutputStream[out=").append(this.out.toString()).append(",count=").append(this.count).append("]").toString();
  }

}
