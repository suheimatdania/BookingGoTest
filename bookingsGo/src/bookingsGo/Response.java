package bookingsGo;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

@SerializedName("supplier_id")
@Expose
public String supplierId;
@SerializedName("pickup")
@Expose
public String pickup;
@SerializedName("dropoff")
@Expose
public String dropoff;
@SerializedName("options")
@Expose
public List<Option> options = null;

}