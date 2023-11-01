package dev.godjango.tfprotocol.constants;


public final class TfprotocolConsts {
    private TfprotocolConsts(){}
    public final static class PutGetCommand{
        private PutGetCommand(){}
        public static long HPFEND = 0;
        public static long HPFSTOP = -1;
        public static long HPFCANCEL = -2;
        public static long HPFCONT = -3;
        public static long HPFFIN = -127;
    }
    public enum FSTYPE{
        DIRECTORY,
        CHARACTER_DEVICE,
        BLOCK_DEVICE,
        REGULAR_FILE,
        FIFO_OR_PIPE,
        SYMBOLINK,
        SOCKET,
        OTHER,
        ERROR
    }
}
