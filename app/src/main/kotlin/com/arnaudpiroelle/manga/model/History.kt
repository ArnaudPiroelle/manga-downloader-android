package com.arnaudpiroelle.manga.model

import se.emilsjolander.sprinkles.Model
import se.emilsjolander.sprinkles.annotations.AutoIncrement
import se.emilsjolander.sprinkles.annotations.Column
import se.emilsjolander.sprinkles.annotations.Key
import se.emilsjolander.sprinkles.annotations.Table

@Table("Histories")
class History : Model() {
    @Key
    @AutoIncrement
    @Column("id")
    var id: Long = 0

    @Column("label")
    var label: String? = null

    @Column("date")
    var date: Long = 0

}
