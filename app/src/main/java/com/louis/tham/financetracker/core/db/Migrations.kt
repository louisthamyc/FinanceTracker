package com.louis.tham.financetracker.core.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE transactions_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                amount TEXT NOT NULL,
                category TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                note TEXT NOT NULL
            )""".trimIndent()
        )
        db.execSQL(
            """INSERT INTO transactions_new (id, title, amount, category, type, date, timestamp, note)
                SELECT id, title, CAST(amount AS TEXT), category, type, date, timestamp, note
                FROM transactions""".trimIndent()
        )
        db.execSQL("DROP TABLE transactions")
        db.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
    }
}
