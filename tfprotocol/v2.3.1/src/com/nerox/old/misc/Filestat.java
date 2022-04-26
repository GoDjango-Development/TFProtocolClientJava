package com.nerox.old.misc;

import java.sql.Timestamp;
import java.util.Date;

public class Filestat {

    typeEnum type;
    long size;
    long lastAccess;
    long lastMod;

    /**
     * Constructor de la clase
     *
     * @param type
     *            Enumerador encargado de clasificar el fichero
     * @see typeEnum
     * @param size
     *            Tamaño del fichero
     * @param last_access
     *            último acceso al fichero
     * @param last_mod
     *            último modificación al fichero
     */
    public Filestat(typeEnum type, long size, long last_access, long last_mod) {
        this.type = type;
        this.size = size;
        this.lastAccess = last_access;
        this.lastMod = last_mod;
    }
    /**
     * Constructor de la clase
     *
     * @param type
     *            Enumerador encargado de clasificar el fichero
     * @see typeEnum
     * @param size
     *            Tamaño del fichero
     * @param last_access
     *            último acceso al fichero
     * @param last_mod
     *            último modificación al fichero
     */
    public Filestat(char type, long size, long last_access, long last_mod) {
        switch (type){
            case 'F':
                this.type = typeEnum.FILE;
                break;
            case 'D':
                this.type = typeEnum.DIR;
                break;
            default:
                this.type = typeEnum.UNKNOWN;
                break;
        }
        this.size = size;
        this.lastAccess = last_access;
        this.lastMod = last_mod;
    }

    public typeEnum getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public long getLastMod() {
        return lastMod;
    }

    public Date getLastModDate() {
        Timestamp stamp = new Timestamp(this.lastMod);
        return new Date(stamp.getTime());
    }

    @Override
    public String toString() {
        return "Filestat{" + "type=" + type + ", size=" + size + ", last_access=" + this.lastAccess + ", last_mod=" + this.lastMod
                + '}';
    }

    public enum typeEnum {
        DIR, FILE, UNKNOWN
    }
}
