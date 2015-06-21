package com.arnaudpiroelle.manga.model;

import java.util.Date;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Histories")
public class History extends Model {
    @Key
    @AutoIncrement
    @Column("id")
    private long id;

    @Column("label")
    private String label;

    @Column("date")
    private long date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public static class HistoryBuilder {
        private History history;

        public HistoryBuilder() {
            history = new History();
        }

        public static HistoryBuilder createHisotry() {
            return new HistoryBuilder();
        }

        public HistoryBuilder withLabel(String label) {
            history.setLabel(label);
            return this;
        }

        public HistoryBuilder withDate(Date date) {
            history.setDate(date.getTime());
            return this;
        }

        public HistoryBuilder withId(long id) {
            history.setId(id);
            return this;
        }

        public History build() {
            return history;
        }
    }
}
