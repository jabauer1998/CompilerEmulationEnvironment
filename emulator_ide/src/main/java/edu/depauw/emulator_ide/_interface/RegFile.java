package edu.depauw.emulator_ide._interface;

public interface RegFile {
    public long getRegisterValue(String regName);

   public void setRegisterValue(String regName, long regValue);
}
