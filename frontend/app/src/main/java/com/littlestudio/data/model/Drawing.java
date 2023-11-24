package com.littlestudio.data.model;

import java.util.Date;
import java.util.List;

public class Drawing {
    public int id;
    public String title;
    public String description;
    public String image_url;
    public String jumping_url;
    public String dab_url;
    public String wave_hello_url;
    public String type;
    public int host_id;
    public List<User> participants;
    public int voice_id;
    public Date created_at;
    public Date updated_at;
}

