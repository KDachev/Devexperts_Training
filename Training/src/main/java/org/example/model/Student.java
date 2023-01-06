package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student{
    private String year;

    private Integer age;

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

//    @Override
//    public int compareTo(Student o) {
////        if(this.count.equals(o.getCount())){
////            return this.area.compareTo(o.getArea());
////        } else {
////            return (this.count - o.getCount()) > 0 ? 1 : -1;
////        }
//
//        if(!this.count.equals(o.getCount())){
//            return (this.count - o.getCount()) > 0 ? 1 : -1;
//        }
//        if(!this.area.equals(o.getArea())){
//            return (this.area - o.getArea()) > 0 ? 1 : -1;
//        }
//        if(!this.sex.equals(o.getSex())){
//            return (this.sex.compareTo(o.getSex())) > 0 ? 1 : -1;
//        }
//        if(!this.ethnic.equals(o.getEthnic())){
//            return (this.ethnic.compareTo(o.getEthnic())) > 0 ? 1 : -1;
//        }
//        if(!this.age.equals(o.getAge())){
//            return (this.age - o.getAge()) > 0 ? 1 : -1;
//        }
//        if(!this.year.equals(o.getYear())){
//            return (this.year.compareTo(o.getYear())) > 0 ? 1 : -1;
//        }
//        return 0;
//    }

    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if(!(o instanceof Student))
            return false;
        Student other = (Student)o;
        return year.equals(other.getYear())
                || age.equals(other.getAge())
                || ethnic.equals(other.getEthnic())
                || sex.equals(other.sex)
                || area.equals(other.getArea())
                || count.equals(other.getCount());
    }

    @Override
    public final int hashCode() {
        int result = 17;
        if (year != null) {
            result = 31 * result + year.hashCode();
        }
        if (age != null) {
            result = 31 * result + age.hashCode();
        }
        if (ethnic != null) {
            result = 31 * result + ethnic.hashCode();
        }
        if (sex != null) {
            result = 31 * result + sex.hashCode();
        }
        if (area != null) {
            result = 31 * result + area.hashCode();
        }
        if (count != null) {
            result = 31 * result + count.hashCode();
        }
        return result;
    }
}
