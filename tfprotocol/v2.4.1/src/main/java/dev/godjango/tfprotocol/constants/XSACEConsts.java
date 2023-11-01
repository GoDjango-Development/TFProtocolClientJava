package dev.godjango.tfprotocol.constants;

public class XSACEConsts {
    public static class Commands{
        public static long ERROR = -128000;
        public static long FINISHED = -129000;
        public static long OK = -127000;
        public static long SIGKILL = -130000;
        public static long SIGTERM = -131000;
        public static long SIGUSR1 = -132000;
        public static long SIGUSR2 = -133000;
        public static short ERROR_BUFFER = 256;
    }
}

