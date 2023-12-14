package com.example.data.storage.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.storage.user.UserTable.tableName
import com.example.domain.Logger
import javax.inject.Inject

class From1to2 @Inject constructor(
    private val logger: Logger,
) : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        logger.d("Starting DB migration")
        migrateUser(db)
        logger.d("Ending DB migration")
    }

    private fun migrateUser(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE `$tableName`
            ADD COLUMN pub_year INTEGER
            """
        )
    }
}
// {
//"gender": "male",
//"name": {
//"title": "Mr",
//"first": "Tyrone",
//"last": "Hopkins"
//},
//"location": {
//"street": {
//"number": 4144,
//"name": "Green Rd"
//},
//"city": "Palmdale",
//"state": "New Jersey",
//"country": "United States",
//"postcode": 18593,
//"coordinates": {
//"latitude": "84.7706",
//"longitude": "-35.2653"
//},
//"timezone": {
//"offset": "-10:00",
//"description": "Hawaii"
//}
//},
//"email": "tyrone.hopkins@example.com",
//"login": {
//"uuid": "f6165a91-8bf7-4f29-80ae-f0088ecabab3",
//"username": "purpletiger991",
//"password": "13131313",
//"salt": "FU9E8lKG",
//"md5": "aaa2df59d9b0d9f1bd86bf1ef19deff5",
//"sha1": "2837991715c78ca0cf56ffcb6e257095dbd5d4d7",
//"sha256": "76fe1f1068070d00de90f7486c29b22683c7fd95dd05430299792578be3a8432"
//},
//"dob": {
//"date": "1945-09-05T02:40:37.924Z",
//"age": 78
//},
//"registered": {
//"date": "2003-04-24T06:03:28.547Z",
//"age": 20
//},
//"phone": "(697) 766-7254",
//"cell": "(788) 380-7896",
//"id": {
//"name": "SSN",
//"value": "410-30-3838"
//},
//"picture": {
//"large": "https://randomuser.me/api/portraits/men/54.jpg",
//"medium": "https://randomuser.me/api/portraits/med/men/54.jpg",
//"thumbnail": "https://randomuser.me/api/portraits/thumb/men/54.jpg"
//},
//"nat": "US"
//},
