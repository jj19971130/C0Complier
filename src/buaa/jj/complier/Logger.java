package buaa.jj.complier;

import java.io.PrintStream;

public class Logger extends PrintStream {
    int num = 1;
    boolean state = false;
    int offset = 0;

    StringBuilder buffer = new StringBuilder();

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
            buffer.reverse();
        } else {
            this.state = false;
            print(buffer);
        }
    }

    public void insertJmp() {
        int i = buffer.lastIndexOf("JMP 0,0");
        i += 6;
        buffer.replace(i,i + 1,++num + "");
    }

    public void insertJmc() {
        int i = buffer.lastIndexOf("JMC 0,0");
        i += 6;
        buffer.replace(i,i + 1,++num + "");
    }

    @Override
    public void println(String x) {
        num++;
        if (state) {
            buffer.append(x + '\n');
        } else
            super.println(x);
    }
}
