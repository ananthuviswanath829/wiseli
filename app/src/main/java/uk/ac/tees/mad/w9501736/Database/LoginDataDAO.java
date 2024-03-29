package uk.ac.tees.mad.w9501736.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import uk.ac.tees.mad.w9501736.models.CircleData;
import uk.ac.tees.mad.w9501736.models.CircleModel;
import uk.ac.tees.mad.w9501736.models.LoginModel;

@Dao
public interface LoginDataDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insert(List<CircleData> locationData);

    @Query("SELECT * FROM circledata")
    public List<CircleData> getCircleData();


    @Query("DELETE FROM circledata")
    public void delete();

    @Query("DELETE FROM circledata WHERE circleId=:id")
    public void delete(String id);
//
//
}
