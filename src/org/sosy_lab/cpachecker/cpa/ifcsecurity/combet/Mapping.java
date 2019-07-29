package org.sosy_lab.cpachecker.cpa.ifcsecurity.combet;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}mapitem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"mapitem"})
@XmlRootElement(name = "mapping")
public class Mapping {

  protected List<Mapitem> mapitem;

  /**
   * Gets the value of the mapitem property.
   *
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the mapitem property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   *
   * <pre>
   * getMapitem().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list {@link Mapitem }
   *
   *
   */
  public List<Mapitem> getMapitem() {
    if (mapitem == null) {
      mapitem = new ArrayList<>();
    }
    return this.mapitem;
  }

}
