package io.github.h20man13.emulator_ide._interface;

public interface Flags {
    public long getStatusValue(String statusName);

    public void setStatusValue(String statusName, long statusValue);
}
