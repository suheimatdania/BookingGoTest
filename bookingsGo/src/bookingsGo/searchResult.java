package bookingsGo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class searchResult {
	@SerializedName("car_type")
	@Expose
	public String carType;
	@SerializedName("price")
	@Expose
	public Integer price;

}
