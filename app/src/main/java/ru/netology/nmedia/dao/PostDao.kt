package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE localId = :localId")
    suspend fun searchPost(localId: Long): PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likedById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
            shareCount = shareCount + 1
             WHERE id = :id;
        """
    )
    suspend fun shareById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
            viewsCount = viewsCount + 1
             WHERE id = :id;
        """
    )
    suspend fun viewById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE localId = :localId")
    suspend fun removeBylocalId(localId: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

//    @Query("SELECT id FROM PostEntity WHERE localId = :localId")
//    suspend fun findId(localId: Long): Long
}