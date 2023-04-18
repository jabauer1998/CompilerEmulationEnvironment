package io.github.h20man13.emulator_ide._interface;

public interface Machine extends Memory, RegFile, Flags {
    public void appendIoText(String Name, String ioText);

    public void writeIoText(String Name, String toWrite);

    public String readIoText(String Name);
}
