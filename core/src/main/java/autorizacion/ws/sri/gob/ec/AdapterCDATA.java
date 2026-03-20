package autorizacion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class AdapterCDATA extends XmlAdapter<String, String> {
    public AdapterCDATA() {
    }

    public String marshal(String arg0) throws Exception {
        return "<![CDATA[" + arg0 + "]]>";
    }

    public String unmarshal(String arg0) throws Exception {
        return arg0;
    }
}
