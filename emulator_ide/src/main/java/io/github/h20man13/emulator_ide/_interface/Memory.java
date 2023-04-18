package io.github.h20man13.emulator_ide._interface;

public interface Memory {
    public void setMemoryValue(int address, long dataValue);

    public long getMemoryValue(int adress);
}
