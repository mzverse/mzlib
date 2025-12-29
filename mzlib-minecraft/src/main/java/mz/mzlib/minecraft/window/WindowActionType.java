package mz.mzlib.minecraft.window;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperCreator;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

// TODO: test v_1400
@WrapMinecraftClass({
    @VersionName(name = "int", remap = false, end = 900),
    @VersionName(name = "net.minecraft.util.ItemAction", begin = 900, end = 1400),
    @VersionName(name = "net.minecraft.container.SlotActionType", begin = 1400, end = 1600),
    @VersionName(name = "net.minecraft.screen.slot.SlotActionType", begin = 1600)
})
public interface WindowActionType extends WrapperObject
{
    WrapperFactory<WindowActionType> FACTORY = WrapperFactory.of(WindowActionType.class);
    @Deprecated
    @WrapperCreator
    static WindowActionType create(Object wrapped)
    {
        return WrapperObject.create(WindowActionType.class, wrapped);
    }

    /**
     * When finish dragging, There will be multiple actions <br/>
     * Drag begins when data%4==0 <br/>
     * Drag a slot when data%4==1 <br/>
     * Drag ends when data%4==2 <br/>
     * Drag with left-button when data/4==0 <br/>
     * Drag with right-button when data/4==1 <br/>
     * Drag with middle-button when data/4==2 <br/>
     */
    static WindowActionType drag()
    {
        return FACTORY.getStatic().static$drag();
    }

    WindowActionType static$drag();

    @SpecificImpl("static$drag")
    @VersionRange(end = 900)
    default WindowActionType static$dragV_900()
    {
        return create(5);
    }

    @SpecificImpl("static$drag")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12268", end = 1400),
        @VersionName(name = "field_7789", begin = 1400)
    })
    WindowActionType static$dragV900();

    /**
     * Left-click or right-click
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    static WindowActionType click()
    {
        return FACTORY.getStatic().static$click();
    }

    WindowActionType static$click();

    @SpecificImpl("static$click")
    @VersionRange(end = 900)
    default WindowActionType static$clickV_900()
    {
        return create(0);
    }

    @SpecificImpl("static$click")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12263", end = 1400),
        @VersionName(name = "field_7790", begin = 1400)
    })
    WindowActionType static$clickV900();

    /**
     * Use shortcut keys to exchange items (1~9 or F)
     * The action data is the swap slot of player inventory (0~8 or 40)
     */
    static WindowActionType swap()
    {
        return FACTORY.getStatic().static$swap();
    }

    WindowActionType static$swap();

    @SpecificImpl("static$swap")
    @VersionRange(end = 900)
    default WindowActionType static$swapV_900()
    {
        return create(2);
    }

    @SpecificImpl("static$swap")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12265", end = 1400),
        @VersionName(name = "field_7791", begin = 1400)
    })
    WindowActionType static$swapV900();

    /**
     * Double-click an item
     */
    static WindowActionType pickUpAll()
    {
        return FACTORY.getStatic().static$pickUpAll();
    }

    WindowActionType static$pickUpAll();

    @SpecificImpl("static$pickUpAll")
    @VersionRange(end = 900)
    default WindowActionType static$pickUpAllV_900()
    {
        return create(6);
    }

    @SpecificImpl("static$pickUpAll")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12269", end = 1400),
        @VersionName(name = "field_7793", begin = 1400)
    })
    WindowActionType static$pickUpAllV900();

    /**
     * Shift-click a slot
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    static WindowActionType shiftClick()
    {
        return FACTORY.getStatic().static$shiftClick();
    }

    WindowActionType static$shiftClick();

    @SpecificImpl("static$shiftClick")
    @VersionRange(end = 900)
    default WindowActionType static$shiftClickV_900()
    {
        return create(1);
    }

    @SpecificImpl("static$shiftClick")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12264", end = 1400),
        @VersionName(name = "field_7794", begin = 1400)
    })
    WindowActionType static$shiftClickV900();

    /**
     * Throw item:
     * Throw one item when data==0
     * Throw all items when data==1
     * Click outside without item:
     * index==-999
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    static WindowActionType drop()
    {
        return FACTORY.getStatic().static$drop();
    }

    WindowActionType static$drop();

    @SpecificImpl("static$drop")
    @VersionRange(end = 900)
    default WindowActionType static$dropV_900()
    {
        return create(4);
    }

    @SpecificImpl("static$drop")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12267", end = 1400),
        @VersionName(name = "field_7795", begin = 1400)
    })
    WindowActionType static$dropV900();

    /**
     * Middle-click in creative-mode or middle-click with item
     */
    static WindowActionType copy()
    {
        return FACTORY.getStatic().static$copy();
    }

    WindowActionType static$copy();

    @SpecificImpl("static$copy")
    @VersionRange(end = 900)
    default WindowActionType static$copyV_900()
    {
        return create(3);
    }

    @SpecificImpl("static$copy")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12266", end = 1400),
        @VersionName(name = "field_7796", begin = 1400)
    })
    WindowActionType static$copyV900();

    @Override
    String toString();

    @SpecificImpl("toString")
    default String toString$impl()
    {
        if(this.equals(drag()))
            return "DRAG";
        else if(this.equals(click()))
            return "CLICK";
        else if(this.equals(swap()))
            return "SWAP";
        else if(this.equals(pickUpAll()))
            return "PICK_UP_ALL";
        else if(this.equals(shiftClick()))
            return "SHIFT_CLICK";
        else if(this.equals(drop()))
            return "DROP";
        else if(this.equals(copy()))
            return "COPY";
        else
            return "UNKNOWN";
    }
}
