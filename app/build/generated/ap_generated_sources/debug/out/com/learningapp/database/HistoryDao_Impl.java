package com.learningapp.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HistoryEntry> __insertionAdapterOfHistoryEntry;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHistoryEntry = new EntityInsertionAdapter<HistoryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `history` (`id`,`topic`,`scorePercent`,`timestamp`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final HistoryEntry entity) {
        statement.bindLong(1, entity.id);
        if (entity.topic == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.topic);
        }
        statement.bindLong(3, entity.scorePercent);
        statement.bindLong(4, entity.timestamp);
      }
    };
  }

  @Override
  public void insert(final HistoryEntry entry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfHistoryEntry.insert(entry);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<HistoryEntry> getAllHistory() {
    final String _sql = "SELECT * FROM history ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
      final int _cursorIndexOfScorePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "scorePercent");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<HistoryEntry> _result = new ArrayList<HistoryEntry>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final HistoryEntry _item;
        final String _tmpTopic;
        if (_cursor.isNull(_cursorIndexOfTopic)) {
          _tmpTopic = null;
        } else {
          _tmpTopic = _cursor.getString(_cursorIndexOfTopic);
        }
        final int _tmpScorePercent;
        _tmpScorePercent = _cursor.getInt(_cursorIndexOfScorePercent);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item = new HistoryEntry(_tmpTopic,_tmpScorePercent,_tmpTimestamp);
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
