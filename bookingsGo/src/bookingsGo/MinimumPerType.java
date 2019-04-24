package bookingsGo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinimumPerType {
	@SerializedName("car_type")
	@Expose
	public String carType;
	
	@SerializedName("supplier")
	@Expose
	public String supplier;
	
	@SerializedName("price")
	@Expose
	public Integer price;

}
