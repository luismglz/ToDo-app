package com.arasaka.todolist.Model

import android.os.Parcelable
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dateTime: LocalDateTime? = null,
    var status: Boolean = true
):Parcelable

@Dao
interface TaskDao{

    @Query("SELECT * FROM Task WHERE status = 1")
    suspend fun getPendingTasks(): List<Task>


    @Insert(onConflict = REPLACE)
    suspend fun saveNewTask(task: Task):Long

    @Update
    suspend fun updateTask(task: Task)
}

@Database(entities = [Task::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract  class TaskDatabase(): RoomDatabase(){

    abstract fun taskDao(): TaskDao


}


//Convert date for DB can handle it
object  LocalDateTimeConverter{


    //Read date in string from db and return converted to datetime
    @TypeConverter
    fun toDateTime(dateString: String):LocalDateTime?{
        return if(dateString ==null) null else LocalDateTime.parse(dateString)
    }


    //Localdatetime to string
    @TypeConverter
    fun toDateString(date: LocalDateTime?): String?{
        return date?.toString();
    }

}