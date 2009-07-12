package uk.co.brotherlogic.mdb;
/**
 * Class describing an element in a web table cell
 * @author Simon Tucker
 */

public class WebTableCell
{

  //What the table represents
  String data;

  public WebTableCell(String in)
  {
    data = in;
  }

  public WebTableCell()
  {
    data = "";
  }

  public void setString(String in)
  {
    data = in;
  }

  public void addString(String in)
  {
    data += in;
  }

  public String getString()
  {
    return data;
  }

}
