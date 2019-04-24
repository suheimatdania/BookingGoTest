package bookingsGo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option implements Comparable<Option> {

@SerializedName("car_type")
@Expose
public String carType;
@SerializedName("price")
@Expose
public Integer price;
@Override
public int compareTo(Option o) {
	return this.price.compareTo(o.price);
}

}