package sample;

import java.util.LinkedList;
import java.util.jar.Attributes;

class Field
{
    static int[][] field;
    Field()
    {
        field = new int[8][8];
        for (int y = 0; y < 7; y++)
        {
            for (int x = 0; x < 7; x++)
            {
                field[y][x] = 0;
            }
        }
        for (int x = 0; x < 8; x++)
        {
            field[6][x] = -1; //черные пешки
            field[1][x] = 1; // белые пешки
        }
        field[0][0] = 2;
        field[0][7] = 2; //белые башни
        field[7][0] = -2;
        field[7][7] = -2; //черные башни

        field [0][1] = 3;
        field[0][6] = 3;//белые кони
        field[7][1] = -3;
        field[7][6] = -3;//черные кони

        field [0][2] = 4;
        field[0][5] = 4;// белые слоны
        field[7][2] = -4;
        field[7][5] = -4;// черные слоны

        field[0][4] = 6;//белый король
        field[7][4] = -6;//черный король

        field[0][3] = 5;//белая ферзь
        field[7][3] = -5;//черная ферзь

    }

}

class Maxcost
{
    int maxcost;
    Maxcost()
    {
        maxcost = 0;
    }
}

class Coord
{
    int posy;
    int posx;

    Coord()
    {}

    Coord(int y, int x)
    {
        posy = y;
        posx = x;
    }

    boolean isEqual(Coord coord)
    {
        if (this.posx == coord.posx && this.posy == coord.posy) return true;
        return false;
    }
}

abstract class Base
{
    Coord pos;

    boolean startPos;
    boolean dead;

    LinkedList<Coord> mayGo;
    LinkedList<Coord> mayHit;

    short player; // 1-белые сверху, -1 - черные снизу

    static Field field = new Field();

    int index;
    int cost;

    Base()
    {
        startPos = true;
        dead = false;
        mayGo = new LinkedList<Coord>();
        mayHit = new LinkedList<Coord>();
    }

    Base(short _player)
    {
        startPos = true;
        dead = false;
        mayGo = new LinkedList<Coord>();
        mayHit = new LinkedList<Coord>();
        player = _player;
    }

    boolean checkCorrect(int coord)
    {
        if (coord >= 0 && coord < 8) return true;
        return false;
    }

    abstract LinkedList<Coord> maygo(Coord pos);
    abstract LinkedList<Coord> mayhit(Coord pos);

    void go(Coord newpos)
    {
        if (this.dead) return;
        field.field[pos.posy][pos.posx] = 0;
        pos.posy = newpos.posy;
        pos.posx = newpos.posx;
        field.field[pos.posy][pos.posx] = index*player;
        startPos = false;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void refreshMays()
    {
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void setField(int[][] field)
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                this.field.field[y][x] = field[y][x];
            }
        }
    }

    void printMayGo()
    {
        System.out.println(this.pos.posy + " " + this.pos.posx + ":\n");
        for (Coord c: mayGo)
        {
            System.out.println(c.posy + " " + c.posx + "\n");
        }
    }

    void printMayHit()
    {
        System.out.println(this.pos.posy + " " + this.pos.posx + ":\n");
        for (Coord c: mayHit)
        {
            System.out.println(c.posy + " " + c.posx + "\n");
        }
    }
}

class Pawn extends Base
{

    LinkedList<Coord> maygo(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        if (checkCorrect(pos.posy + 1 * player))
            if (field.field[pos.posy + 1 * player][pos.posx] == 0)
                res.add(new Coord(pos.posy + 1 * player, pos.posx));
            else return res;
        if (checkCorrect(pos.posy + 2 * player) && startPos)
        {
            if (field.field[pos.posy + 2 * player][pos.posx] == 0)
                res.add(new Coord(pos.posy + 2 * player, pos.posx));
        }
        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        if (checkCorrect(pos.posy + 1 * player)) {
            if (checkCorrect(pos.posx - 1))
                if (field.field[pos.posy + 1 * player][pos.posx - 1] * player < 0)
                    res.add(new Coord(pos.posy + 1 * player, pos.posx - 1));
            if (checkCorrect(pos.posx + 1))
                if (field.field[pos.posy + 1 * player][pos.posx + 1] * player < 0)
                    res.add(new Coord(pos.posy + 1 * player, pos.posx + 1));
        }
        return res;
    }

    Pawn(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 1;
        cost = 50;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);
    }

}

class Tower extends Base
{

    LinkedList<Coord> maygo(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8;k++) //вниз
        {
            int newposy = k + pos.posy;
            if (checkCorrect(newposy))
                if (field.field[newposy][pos.posx] == 0)
                    res.add(new Coord(newposy, pos.posx));
                else break;
        }
        for (int k = 1; k < 8;k++) //вверх
        {
            int newposy = -k + pos.posy;
            if (checkCorrect(newposy))
                if (field.field[newposy][pos.posx] == 0)
                    res.add(new Coord(newposy, pos.posx));
                else break;
        }
        for (int k = 1; k < 8;k++) //вправо
        {
            int newposx = k + pos.posx;
            if (checkCorrect(newposx))
                if (field.field[pos.posy][newposx] == 0)
                    res.add(new Coord(pos.posy, newposx));
                else break;
        }
        for (int k = 1; k < 8;k++) //влево
        {
            int newposx = -k + pos.posx;
            if (checkCorrect(newposx))
                if (field.field[pos.posy][newposx] == 0)
                    res.add(new Coord(pos.posy, newposx));
                else break;
        }
        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8; k++) //вниз
        {
            if (checkCorrect(pos.posy + k))
            {
                if (field.field[pos.posy + k][pos.posx] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх
        {
            if (checkCorrect(pos.posy - k)) {
                if (field.field[pos.posy - k][pos.posx] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вправо
        {
            if (checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy, pos.posx + k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //влево
        {
            if (checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy, pos.posx - k));
                    break;
                }
            }
        }

        return res;
    }

    Tower(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 2;
        cost = 250;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);
    }


}

class Horse extends Base
{

    LinkedList<Coord> maygo(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k2 = -2; k2 < 3; k2+=4)
        {
            for (int k1 = -1; k1 < 2; k1 +=2)
            {
                if (checkCorrect(pos.posy + k2) && checkCorrect(pos.posx + k1))
                    if (field.field[pos.posy + k2][pos.posx + k1] == 0)
                        res.add(new Coord(pos.posy + k2, pos.posx + k1)); //верх-низ на 2. право-лево на 1
                if (checkCorrect(pos.posy + k1) && checkCorrect(pos.posx + k2))
                    if (field.field[pos.posy + k1][pos.posx + k2] == 0)
                        res.add(new Coord(pos.posy + k1, pos.posx + k2));
            }
        }
        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k2 = -2; k2 < 3; k2+=4)
        {
            for (int k1 = -1; k1 < 2; k1 +=2)
            {
                if (checkCorrect(pos.posy + k2) && checkCorrect(pos.posx + k1))
                    if (field.field[pos.posy + k2][pos.posx + k1] * player < 0)
                        res.add(new Coord(pos.posy + k2, pos.posx + k1)); //верх-низ на 2. право-лево на 1
                if (checkCorrect(pos.posy + k1) && checkCorrect(pos.posx + k2))
                    if (field.field[pos.posy + k1][pos.posx + k2] * player < 0)
                        res.add(new Coord(pos.posy + k1, pos.posx + k2));
            }
        }
        return res;
    }

    Horse(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 3;
        cost = 150;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);
    }
}

class Elephant extends Base
{

    LinkedList<Coord> maygo(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8; k++) //вправо вниз
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx + k))
                if (field.field[pos.posy + k][pos.posx + k] == 0)
                    res.add(new Coord(pos.posy + k, pos.posx + k));
                else break;
        }

        for (int k = 1; k < 8; k++) //влево вверх
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx - k))
                if (field.field[pos.posy - k][pos.posx - k] == 0)
                    res.add(new Coord(pos.posy - k, pos.posx - k));
                else break;
        }

        for (int k = 1; k < 8; k++) //влево вниз
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx - k))
                if (field.field[pos.posy + k][pos.posx - k] == 0)
                    res.add(new Coord(pos.posy + k, pos.posx - k));
                else break;
        }

        for (int k = 1; k < 8; k++) //вправо ввверх
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx + k))
                if (field.field[pos.posy - k][pos.posx + k] == 0)
                    res.add(new Coord(pos.posy - k, pos.posx + k));
                else break;
        }

        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8; k++) //вниз вправо
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy + k][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx + k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх влево
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy - k][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx - k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вниз влево
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy + k][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx - k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх вправо
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy - k][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx + k));
                    break;
                }
            }
        }

        return res;
    }

    Elephant(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 4;
        cost = 150;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);
    }

}

class Ferz extends Base
{
    LinkedList<Coord> maygo(Coord pos)
    {
        //from tower
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8;k++) //вниз
        {
            int newposy = k + pos.posy;
            if (checkCorrect(newposy))
                if (field.field[newposy][pos.posx] == 0)
                    res.add(new Coord(newposy, pos.posx));
                else break;
        }
        for (int k = 1; k < 8;k++) //вверх
        {
            int newposy = -k + pos.posy;
            if (checkCorrect(newposy))
                if (field.field[newposy][pos.posx] == 0)
                    res.add(new Coord(newposy, pos.posx));
                else break;
        }
        for (int k = 1; k < 8;k++) //вправо
        {
            int newposx = k + pos.posx;
            if (checkCorrect(newposx))
                if (field.field[pos.posy][newposx] == 0)
                    res.add(new Coord(pos.posy, newposx));
                else break;
        }
        for (int k = 1; k < 8;k++) //влево
        {
            int newposx = -k + pos.posx;
            if (checkCorrect(newposx))
                if (field.field[pos.posy][newposx] == 0)
                    res.add(new Coord(pos.posy, newposx));
                else break;
        }

        //from elephant
        for (int k = 1; k < 8; k++) //вправо вниз
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx + k))
                if (field.field[pos.posy + k][pos.posx + k] == 0)
                    res.add(new Coord(pos.posy + k, pos.posx + k));
                else break;
        }

        for (int k = 1; k < 8; k++) //влево вверх
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx - k))
                if (field.field[pos.posy - k][pos.posx - k] == 0)
                    res.add(new Coord(pos.posy - k, pos.posx - k));
                else break;
        }

        for (int k = 1; k < 8; k++) //влево вниз
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx - k))
                if (field.field[pos.posy + k][pos.posx - k] == 0)
                    res.add(new Coord(pos.posy + k, pos.posx - k));
                else break;
        }

        for (int k = 1; k < 8; k++) //вправо ввверх
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx + k))
                if (field.field[pos.posy - k][pos.posx + k] == 0)
                    res.add(new Coord(pos.posy - k, pos.posx + k));
                else break;
        }

        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        //from tower
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k = 1; k < 8; k++) //вниз
        {
            if (checkCorrect(pos.posy + k)) {
                if (field.field[pos.posy + k][pos.posx] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх
        {
            if (checkCorrect(pos.posy - k)) {
                if (field.field[pos.posy - k][pos.posx] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вправо
        {
            if (checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy, pos.posx + k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //влево
        {
            if (checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy, pos.posx - k));
                    break;
                }
            }
        }

        //from elephant
        for (int k = 1; k < 8; k++) //вниз вправо
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy + k][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx + k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх влево
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy - k][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx - k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вниз влево
        {
            if (checkCorrect(pos.posy + k) && checkCorrect(pos.posx - k)) {
                if (field.field[pos.posy + k][pos.posx - k] * player > 0)
                    break;
                if (field.field[pos.posy + k][pos.posx - k] * player < 0) {
                    res.add(new Coord(pos.posy + k, pos.posx - k));
                    break;
                }
            }
        }

        for (int k = 1; k < 8; k++) //вверх вправо
        {
            if (checkCorrect(pos.posy - k) && checkCorrect(pos.posx + k)) {
                if (field.field[pos.posy - k][pos.posx + k] * player > 0)
                    break;
                if (field.field[pos.posy - k][pos.posx + k] * player < 0) {
                    res.add(new Coord(pos.posy - k, pos.posx + k));
                    break;
                }
            }
        }

        return res;
    }

    Ferz(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 5;
        cost = 450;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);

    }
}

class King extends Base
{

    LinkedList<Coord> maygo(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k1 = -1; k1 < 2; k1++)
        {
            for (int k2 = -1; k2 < 2; k2++)
            {
                if (k1 == 0 && k2 == 0) continue;
                if (checkCorrect(pos.posy + k1) && checkCorrect(pos.posx + k2))
                    if (field.field[pos.posy + k1][pos.posx + k2] == 0)
                        res.add(new Coord(pos.posy + k1, pos.posx + k2));
            }
        }

        return res;
    }

    LinkedList<Coord> mayhit(Coord pos)
    {
        LinkedList<Coord> res = new LinkedList<Coord>();
        for (int k1 = -1; k1 < 2; k1++)
        {
            for (int k2 = -1; k2 < 2; k2++)
            {
                if (k1 == 0 && k2 == 0) continue;
                if (checkCorrect(pos.posy + k1) && checkCorrect(pos.posx + k2))
                    if (field.field[pos.posy + k1][pos.posx + k2] * player < 0)
                        res.add(new Coord(pos.posy + k1, pos.posx + k2));
            }
        }

        return res;
    }

    King(short _player, int _posy, int _posx)
    {
        super(_player);
        pos = new Coord(_posy, _posx);
        index = 6;
        if (_player == -1)cost = 90000;
        else cost = 45000;
        mayGo = maygo(pos);
        mayHit = mayhit(pos);
    }

    void go(Coord newpos)
    {
        super.go(newpos);
    }
}

abstract class Gamer
{
    Pawn pawns[];
    Tower towers[];
    Horse horses[];
    Elephant elephants[];
    Ferz ferz;
    King king;
    LinkedList<Base> figs;

    void printFigsCosts()
    {
        for (Base f: figs)
        {
            System.out.println(f.cost);
        }
    }

    static Base findFigFromPosition(Coord pos, LinkedList<Base> figs)
    {
        for (Base f: figs)
        {
            if (f.pos.posx == pos.posx && f.pos.posy == pos.posy && !f.dead) return f;
        }
        return null;
    }
}

class Player extends Gamer //белые сверху
{
    static short player = 1;

    Player()
    {
        figs = new LinkedList<Base>();

        pawns = new Pawn[8];//белые пешки
        for (int i = 0; i < 8; i++)
        {
            pawns[i] = new Pawn(player,1,i);
            figs.add(pawns[i]);
        }

        towers = new Tower[2];//белые башни
        towers[0] = new Tower(player, 0, 0);
        figs.add(towers[0]);
        towers[1] = new Tower(player,0,7);
        figs.add(towers[1]);

        horses = new Horse[2];//белые кони
        horses[0] = new Horse(player, 0, 1);
        figs.add(horses[0]);
        horses[1] = new Horse(player, 0, 6);
        figs.add(horses[1]);

        elephants = new Elephant[2];//белые слоны
        elephants[0] = new Elephant(player, 0, 2);
        figs.add(elephants[0]);
        elephants[1] = new Elephant(player, 0 , 5);
        figs.add(elephants[1]);

        ferz = new Ferz(player,0,3); // белая ферзь
        figs.add(ferz);

        king = new King(player,0,4);//белый король
        figs.add(king);
    }

}

class Bot extends Gamer //черные снизу
{
    static short player = -1;

    Bot()
    {
        figs = new LinkedList<Base>();

        pawns = new Pawn[8];//белые пешки
        for (int i = 0; i < 8; i++)
        {
            pawns[i] = new Pawn(player,6,i);
            figs.add(pawns[i]);
        }

        towers = new Tower[2];//белые башни
        towers[0] = new Tower(player, 7, 0);
        figs.add(towers[0]);
        towers[1] = new Tower(player,7,7);
        figs.add(towers[1]);

        horses = new Horse[2];//белые кони
        horses[0] = new Horse(player, 7, 1);
        figs.add(horses[0]);
        horses[1] = new Horse(player, 7, 6);
        figs.add(horses[1]);

        elephants = new Elephant[2];//белые слоны
        elephants[0] = new Elephant(player, 7, 2);
        figs.add(elephants[0]);
        elephants[1] = new Elephant(player, 7 , 5);
        figs.add(elephants[1]);

        ferz = new Ferz(player,7,3); // белая ферзь
        figs.add(ferz);

        king = new King(player,7,4);//белый король
        figs.add(king);

    }
}

class Atributes
{
    int cost;
    Coord from;
    Coord to;

    Atributes()
    {}

    Atributes(int cost, Coord from, Coord to)
    {
        this.cost = cost;
        this.from = from;
        this.to = to;
    }

    void printAtributes()
    {
        System.out.println(cost + " from " + from.posy + " " + from.posx + " to " + to.posy +" " + to.posx + "\n");
    }

    Atributes max(Atributes atr1)
    {
        if (this.cost > atr1. cost)
            return this;
        else return atr1;
    }

    Atributes min(Atributes atr1)
    {
        if (this.cost < atr1. cost)
            return this;
        else return atr1;
    }

    Atributes copy()
    {
        if (this == null) return null;
        Atributes res = new Atributes(this.cost, this.from, this.to);
        return res;
    }

}

public class Figures {
    int field[][];
    static int fieldCost[][];
    Player player;
    Bot bot;

    LinkedList<Base> copy(LinkedList<Base> list)
    {
        LinkedList<Base> res = new LinkedList<Base>();
        for (Base l: list)
        {
            res.add(l);
        }
        return res;
    }

    int[][] copy(int[][]field)
    {
        int res[][] = new int[8][8];
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                res[i][j] = field[i][j];
            }
        }
        return res;
    }

    Figures()
    {
        player = new Player();
        bot = new Bot();
        field = copy(player.pawns[0].field.field);

        fieldCost = new int[8][8];
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                fieldCost[y][x] = 0;
                fieldCost[y][x] += y;
                if (x <4) fieldCost[y][x] += (int)((x/2));
                else fieldCost[y][x] += (int) ((7-x)/2);
            }
        }
    }

    void setField(int[][] field)
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                this.field[y][x] = field[y][x];
            }
        }
    }
    void printFieldCost()
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                System.out.print(fieldCost[y][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    void printField()
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                if (field[y][x] >= 0) System.out.print("+");
                System.out.print(field[y][x] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    void printCostsPlayer()
    {
        player.printFigsCosts();
    }

    void printCostsBot()
    {
        bot.printFigsCosts();
    }


    class Collection
    {
        LinkedList<Integer> cost1;
        LinkedList<Integer> cost2;
        LinkedList<Coord> from;
        LinkedList<Coord> to;
        LinkedList<Boolean> added;

        Collection()
        {
            cost1 = new LinkedList<Integer>();
            cost2 = new LinkedList<Integer>();
            from = new LinkedList<Coord>();
            to = new LinkedList<Coord>();
            added = new LinkedList<Boolean>();
        }

        void add(Integer cost1, Integer cost2, Coord from, Coord to)
        {
            this.cost1.add(cost1);
            this.cost2.add(cost2);
            this.from.add(from);
            this.to.add(to);
            this.added.add(true);
        }

        boolean contains(Integer cost1)
        {
            if (this.cost1.contains(cost1)) return true;
            return false;
        }

        boolean contains(Coord from, Coord to)
        {
            for (Coord f: this.from)
            {
                if (f.isEqual(from))
                {
                    int ind = this.from.indexOf(f);
                    if (this.to.get(ind).isEqual(to))
                        return true;
                }
            }
            return false;
        }

        int getIndexOfCost1(Integer cost1)
        {
            return this.cost1.indexOf(cost1);
        }

        int getIndexOfMove(Coord from, Coord to)
        {
            for (Coord f: this.from)
            {
                if (f.isEqual(from))
                {
                    int ind = this.from.indexOf(f);
                    if (this.to.get(ind).isEqual(to))
                        return ind;
                }
            }
            return -1;
        }

        boolean newDeltaIsLess(Integer cost1, Integer cost2, int index)
        {
            if ( Math.abs(cost1 - cost2) < Math.abs(this.cost1.get(index) - this.cost2.get(index)) )
            {
                return true;
            }
            return false;
        }

        boolean posIsTheSame(Coord from, Coord to, int index)
        {
            if (this.from.get(index).isEqual(from) && this.to.get(index).isEqual(to)) return true;
            return false;
        }

        boolean newCost2IsBigger(Integer cost2, int index)
        {
            return cost2 > this.cost2.get(index);
        }

        void replace(Integer cost1, Integer cost2, Coord from, Coord to, int index)
        {
            this.cost1.set(index,cost1);
            this.cost2.set(index,cost2);
            this.from.set(index,from);
            this.to.set(index,to);
            this.added.set(index,false);
        }

        Coord[] getBestMove()
        {
            Coord res[] = new Coord[2];
            int i = 0;
            int max = -9000;
            for (Integer c1: cost1)
            {
                Integer c2 = this.cost2.get(i);
                if ( (c1 - c2) > max) {
                    max = c1 - c2;
                    res[0] = this.from.get(i);
                    res[1] = this.to.get(i);
                }
                i++;
            }

            if (max > 0) return res;
            return null;

        }

        void printCollection()
        {
            for (Integer c1 : this.cost1)
            {
                int index = getIndexOfCost1(c1);
                int c2 = this.cost2.get(index);
                Coord from = this.from.get(index);
                Coord to = this.to.get(index);
                Boolean added = this.added.get(index);
                System.out.println("\n--------------------\n" + c1 + " " + c2 + " " + " from: " + from.posy + " " + from.posx + " to: " + to.posy + " " + to.posx + " added: " + added + "\n-------------------------------\n");
            }

        }
    }



    Atributes getBestMove(int deep, Coord from, Coord fmove, int cost, Atributes best, int[][] field, LinkedList<Base> botfigs, LinkedList<Base> playerfigs) //initial with (0, null, создать, 0, 0, this.field, bot.figs)
    {
        int saveField[][] = copy(field);
        Coord savefmove = null;
        if (fmove != null)
            savefmove = new Coord(fmove.posy, fmove.posx);

        if (deep == 4) {
            Atributes atributes = new Atributes(cost, from, fmove);
            return atributes;
        }

        if (deep % 2 == 0 && deep < 4) {
            for (Base fig : botfigs) {
                if (fig.dead) continue;

                fig.refreshMays();
                for (Coord newpos : fig.mayHit) // побить
                {
                    Coord savepos = new Coord(fig.pos.posy, fig.pos.posx);
                    Base enemyFig = Gamer.findFigFromPosition(newpos, playerfigs);
                    cost += enemyFig.cost;
                    enemyFig.dead = true;
                    boolean startpos = fig.startPos;
                    fig.go(newpos);
                    if (fmove == null) {
                        fmove = newpos;
                        from = savepos;
                    }

                    LinkedList<Base> newfigsplayer = copy(playerfigs);
                    LinkedList<Base> newfigsbot = copy(botfigs);
                    Atributes newbest = new Atributes(cost, from, fmove);
                    if (best == null)
                        best = getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer);
                    else
                    best = best.max(getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer));
                    fmove = savefmove;
                    fig.go(savepos);
                    fig.startPos = startpos;
                    fig.setField(saveField);
                    enemyFig.dead = false;
                    cost -= enemyFig.cost;
                }

                fig.refreshMays();
                for (Coord newpos : fig.mayGo) { //просто пойти
                    Coord savepos = new Coord(fig.pos.posy, fig.pos.posx);
                    if (fmove == null) {
                        fmove = newpos;
                        from = savepos;
                        cost = fieldCost[newpos.posy][newpos.posx];
                    }
                    boolean startpos = fig.startPos;
                    fig.go(newpos); //сходить новыми фигурами
                    LinkedList<Base> newfigsplayer = copy(playerfigs);
                    LinkedList<Base> newfigsbot = copy(botfigs);
                    Atributes newbest = new Atributes(cost, from, fmove);
                    if (best == null)
                        best = getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer);
                    else
                        best = best.max(getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer));
                    fmove = savefmove;
                    fig.go(savepos);
                    fig.setField(saveField);
                    fig.startPos = startpos;
                }

            }

        }
        if (deep % 2 == 1 && deep < 4) {

            for (Base fig : playerfigs) {
                if (fig.dead) continue;
                fig.refreshMays();
                for (Coord newpos : fig.mayGo) { //просто пойти
                    Coord savepos = new Coord(fig.pos.posy, fig.pos.posx);
                    boolean startpos = fig.startPos;
                    fig.go(newpos); //сходить новыми фигурами
                    LinkedList<Base> newfigsplayer = copy(playerfigs);
                    LinkedList<Base> newfigsbot = copy(botfigs);
                    Atributes newbest = new Atributes(cost, from, fmove);
                    if (best == null)
                        best = getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer);
                    else
                        best = best.min(getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer));
                    fmove = savefmove;
                    fig.go(savepos);
                    fig.startPos = startpos;
                    fig.setField(saveField);
                }

                fig.refreshMays();
                for (Coord newpos : fig.mayHit) // побить
                {
                    Coord savepos = new Coord(fig.pos.posy, fig.pos.posx);
                    Base enemyFig = Gamer.findFigFromPosition(newpos, botfigs);
                    cost -= enemyFig.cost;
                    enemyFig.dead = true;
                    boolean startpos = fig.startPos;
                    fig.go(newpos);
                    LinkedList<Base> newfigsplayer = copy(playerfigs);
                    LinkedList<Base> newfigsbot = copy(botfigs);
                    Atributes newbest = new Atributes(cost, from, fmove);
                    if (best == null)
                        best = getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer);
                    else
                    best = best.min(getBestMove(deep + 1, from, fmove, cost, newbest, fig.field.field, newfigsbot, newfigsplayer));
                    fmove = savefmove;
                    fig.go(savepos);
                    fig.startPos = startpos;
                    fig.setField(saveField);
                    enemyFig.dead = false;
                    cost += enemyFig.cost;
                }
            }

        }
        return best;
    }


}
