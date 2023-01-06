package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Comparable<Student>{
    private String year;

    private int age;

    private String ethnic;

    private String sex;

    private Long area;

    private Long count;

    public Student(String[] fields){
        year = fields[0];
        age = Integer.parseInt(fields[1]);
        ethnic = fields[2];
        sex = fields[3];
        area = Long.parseLong(fields[4]);
        count = Long.parseLong(fields[5]);
    }

    @Override
    public int compareTo(Student o) {
        if(this.count.equals(o.getCount())){
            return this.area.compareTo(o.getArea());
        } else {
            return (this.count - o.getCount()) > 0 ? 1 : -1;
        }
    }
}
