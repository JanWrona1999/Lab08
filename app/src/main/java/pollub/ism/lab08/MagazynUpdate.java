package pollub.ism.lab08;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UpdateAsortymentu")
public class MagazynUpdate {

    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String DATE;
    public String ITEM;
    public int OLD_QUANTITY;
    public int NEW_QUANTITY;

    public MagazynUpdate(String DATE, String ITEM, int OLD_QUANTITY, int NEW_QUANTITY) {
        this.DATE = DATE;
        this.ITEM = ITEM;
        this.OLD_QUANTITY = OLD_QUANTITY;
        this.NEW_QUANTITY = NEW_QUANTITY;
    }
}
