package pollub.ism.lab08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MagazynUpdateDAO {
    @Insert
    public void insert(MagazynUpdate item);

    @Update
    void update(MagazynUpdate item);

    @Query("SELECT * FROM UpdateAsortymentu WHERE ITEM= :selectedItem")
    List<MagazynUpdate> findUpdatesByItemName(String selectedItem);

    @Query("SELECT COUNT(*) FROM UpdateAsortymentu")
    int size();
}
