package drawtools;

import java.util.EventObject;

import com.esri.core.map.Graphic;

/**
 * 
 * @author ropp gispace@yeah.net
 * 
 * ��ͼ�¼���Ŀǰֻ��װ��DRAW_END�¼���������Ҫ�������DRAW_START֮����¼�
 */
public class DrawEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public static final int DRAW_END = 1;

	private Graphic drawGrahic;
	private int type;

	public DrawEvent(Object source, int type, Graphic drawGrahic) {
		super(source);
		this.drawGrahic = drawGrahic;
		this.type = type;
	}

	public Graphic getDrawGraphic() {
		return drawGrahic;
	}

	public int getType() {
		return type;
	}

}
