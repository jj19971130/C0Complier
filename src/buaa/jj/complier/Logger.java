package buaa.jj.complier;

import java.io.PrintStream;

public class Logger extends PrintStream {
    int num = 1;
    boolean state = false;
    int offset = 0;

    private StringBuilder buffer = new StringBuilder();

    public StringBuilder finalCode = new StringBuilder();

    Logger() {
        super(System.out);
    }

    public void clearState() {
        num = 0;
        state = false;
    }

    public void setBuffer(boolean state) {
        if (state) {
            this.state = true;
            buffer.delete(0,buffer.length());
        } else {
            this.state = false;
            finalCode.append(buffer);
            print(buffer);
        }
    }

    public void insertJmp() {
        int i = buffer.lastIndexOf("JMP 0,0");
        i += 6;
        buffer.replace(i,i + 1,num + "");
    }

    public void insertJmc() {
        int i = buffer.lastIndexOf("JMC 0,0");
        i += 6;
        buffer.replace(i,i + 1,num + "");
    }

    @Override
    public void println(String x) {
        num++;
        if (state) {
            buffer.append(x + '\n');
        } else {
            finalCode.append(x + '\n');
            super.println(x);
        }
    }

    @Override
    public void print(String s) {
        super.print(s);
    }
}
