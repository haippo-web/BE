package com.mypage.Model;

import java.sql.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private Long id;
    private Date checkIn;
    private Date checkOut;
    private String status;
    private Long userId;
}
