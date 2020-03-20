package com.kds.gold.acebird.models;

import java.io.Serializable;
import java.util.List;

public class MovieModel implements Serializable {
    String id,name,description,category_id,director,actors,year,added,rating_last_update,age,rating_kinopoisk,
            screenshot_uri,genres_str,cmd,week_and_more,rate;
    int time,hd,status,count,is_movie,lock,fav,has_files,is_series;
    List<String >episoodeList;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGenres_str() {
        return genres_str;
    }

    public void setGenres_str(String genres_str) {
        this.genres_str = genres_str;
    }

    public String getRating_kinopoisk() {
        return rating_kinopoisk;
    }

    public void setRating_kinopoisk(String rating_kinopoisk) {
        this.rating_kinopoisk = rating_kinopoisk;
    }

    public String getRating_last_update() {
        return rating_last_update;
    }

    public void setRating_last_update(String rating_last_update) {
        this.rating_last_update = rating_last_update;
    }

    public String getScreenshot_uri() {
        return screenshot_uri;
    }

    public void setScreenshot_uri(String screenshot_uri) {
        this.screenshot_uri = screenshot_uri;
    }

    public String getWeek_and_more() {
        return week_and_more;
    }

    public void setWeek_and_more(String week_and_more) {
        this.week_and_more = week_and_more;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHd() {
        return hd;
    }

    public void setHd(int hd) {
        this.hd = hd;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getHas_files() {
        return has_files;
    }

    public void setHas_files(int has_files) {
        this.has_files = has_files;
    }

    public int getIs_movie() {
        return is_movie;
    }

    public void setIs_movie(int is_movie) {
        this.is_movie = is_movie;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public String  getRate() {
        return rate;
    }

    public void setRate(String  rate) {
        this.rate = rate;
    }

    public int getIs_series() {
        return is_series;
    }

    public void setIs_series(int is_series) {
        this.is_series = is_series;
    }

    public List<String> getEpisoodeList() {
        return episoodeList;
    }

    public void setEpisoodeList(List<String> episoodeList) {
        this.episoodeList = episoodeList;
    }
}
