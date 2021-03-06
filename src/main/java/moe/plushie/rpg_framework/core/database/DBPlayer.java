package moe.plushie.rpg_framework.core.database;

public class DBPlayer {
    
    public static final DBPlayer MISSING = new DBPlayer(-1);
    
    private final int id;
    
    public DBPlayer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
