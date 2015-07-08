package obj.env;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import func.Math2D;
import gfx.Shape;
import com.jogamp.opengl.util.texture.Texture;
import obj.prim.Drawable;
import obj.prim.Environmental;
import obj.prim.Physical;


public class Wall extends FloorBlock {
	private static List<Wall> wallList = new ArrayList<Wall>();
	
	public Wall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		super(x1, y1, z1, x2, y2, z2, tex);
		
		type = T_WALL;
		
		shape = Shape.createWall("Wall", x1, y1, z1, x2, y2, z2, tex);//new Shape(Graphics3D.createWall(x1, y1, z1, x2, y2, z2, tex));
		wallList.add(this);
	}
	
	public static boolean collideAll(Physical other) {
		for(Wall w : wallList)
			w.collide(other);
			
		return false;
	}
	
		//PARENT FUNCTIONS
		public boolean draw(Graphics2D g) {
			return true;
		}
		
		public boolean collide(Physical other) {			
			float x, y, z;
			x = other.getX();
			y = other.getY();
			z = other.getZ();
			
			float topZ, bottomZ;
			topZ = Math.max(z1, z2);
			bottomZ = Math.min(z1, z2);
			
			
			/*if(otherCol.typeNum != 0 && otherCol.typeNum != 2 && z < topZ-2 && z+16 > bottomZ)
			{
			    /*animated_col_wall(otherCol.x1,otherCol.y1,otherCol.x1,otherCol.y2);
			    animated_col_wall(otherCol.x2,otherCol.y1,otherCol.x2,otherCol.y2);
			    animated_col_wall(otherCol.x1,otherCol.y1,otherCol.x2,otherCol.y1);
			    animated_col_wall(otherCol.x1,otherCol.y2,otherCol.x2,otherCol.y2);
			    
			if (x >= leftX && x <= rightX) && (collision_line((otherCol.x + length)-length+3,(otherCol.y + width)-width,(otherCol.x + length)+length-3,(otherCol.y + width)-width,parPlayer,1,0) || collision_line((otherCol.x + length)-length+3,(otherCol.y + width)+width,(otherCol.x + length)+length-3,(otherCol.y + width)+width,parPlayer,1,0))
			        y = yprevious;
			    if (y >= topY && y <= bottomY) && (collision_line((otherCol.x + length)-length,(otherCol.y + width)-width+3,(otherCol.x + length)-length,(otherCol.y + width)+width-3,parPlayer,1,0) || collision_line((otherCol.x + length)+length,(otherCol.y + width)-width+3,(otherCol.x + length)+length,(otherCol.y + width)+width-3,parPlayer,1,0))
			        x = xprevious;
			    var space,dir;
			    space = 6;//5
			    if(x > (otherCol.x+length)-length-space && x < (otherCol.x+length)+length+space && y < (otherCol.y+width)+width+space && y > (otherCol.y+width)-width-space)
			    {
			        dir = round(point_direction((otherCol.x+length),(otherCol.y+width),x,y)/90)*90;
			        x += calcLen_x(3,dir)
			        y += calcLen_y(3,dir)
			    }
			}*/
			
			if(type == T_WALL)
				if(z+2 < topZ && z+16 > bottomZ)
			    return other.collideLine(x1, y1, x2, y2);
			
			return false;
		}
}
