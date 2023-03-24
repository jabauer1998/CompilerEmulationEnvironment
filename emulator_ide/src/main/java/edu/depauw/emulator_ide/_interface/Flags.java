package edu.depauw.emulator_ide._interface;

public interface Flags {
    public long getStatusValue(String statusName);

    public void setStatusValue(String statusName, long statusValue);
}
