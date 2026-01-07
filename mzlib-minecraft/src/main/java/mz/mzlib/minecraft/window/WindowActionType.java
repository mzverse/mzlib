package mz.mzlib.minecraft.window;

import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

// TODO: test v_1400
@WrapMinecraftClass({
    @VersionName(name = "int", remap = false, end = 900),
    @VersionName(name = "net.minecraft.util.ItemAction", begin = 900, end = 1400),
    @VersionName(name = "net.minecraft.container.SlotActionType", begin = 1400, end = 1600),
    @VersionName(name = "net.minecraft.screen.slot.SlotActionType", begin = 1600, end = 2601),
    @VersionName(name = "net.minecraft.world.inventory.ContainerInput", begin = 2601, remap = false)
})
public interface WindowActionType extends WrapperObject
{
    WrapperFactory<WindowActionType> FACTORY = WrapperFactory.of(WindowActionType.class);

    /**
     * When finish dragging, There will be multiple actions <br/>
     * Drag begins when data%4==0 <br/>
     * Drag a slot when data%4==1 <br/>
     * Drag ends when data%4==2 <br/>
     * Drag with left-button when data/4==0 <br/>
     * Drag with right-button when data/4==1 <br/>
     * Drag with middle-button when data/4==2 <br/>
     */
    WindowActionType DRAG = FACTORY.getStatic().static$DRAG();

    /**
     * Left-click or right-click
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    WindowActionType CLICK = FACTORY.getStatic().static$CLICK();

    /**
     * Use shortcut keys to exchange items (1~9 or F)
     * The action data is the swap slot of player inventory (0~8 or 40)
     */
    WindowActionType SWAP = FACTORY.getStatic().static$SWAP();

    /**
     * Double-click an item
     */
    WindowActionType PICKUP_ALL = FACTORY.getStatic().static$PICKUP_ALL();

    /**
     * Shift-click a slot
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    WindowActionType SHIFT_CLICK = FACTORY.getStatic().static$SHIFT_CLICK();

    /**
     * Throw item:
     * Throw one item when data==0
     * Throw all items when data==1
     * Click outside without item:
     * index==-999
     * The action data is either 0 (left-click) or 1 (right-click)
     */
    WindowActionType DROP = FACTORY.getStatic().static$DROP();

    /**
     * Middle-click in creative-mode or middle-click with item
     */
    WindowActionType CLONE = FACTORY.getStatic().static$CLONE();

    @Override
    String toString();


    WindowActionType static$DRAG();
    @SpecificImpl("static$DRAG")
    @VersionRange(end = 900)
    default WindowActionType static$DRAG_V_900()
    {
        return FACTORY.create(5);
    }
    @SpecificImpl("static$DRAG")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12268", end = 1400),
        @VersionName(name = "field_7789", begin = 1400, end = 2601),
        @VersionName(name = "QUICK_CRAFT", remap = false, begin = 2601)
    })
    WindowActionType static$DRAG_V900();

    WindowActionType static$CLICK();
    @SpecificImpl("static$CLICK")
    @VersionRange(end = 900)
    default WindowActionType static$CLICK_V_900()
    {
        return FACTORY.create(0);
    }
    @SpecificImpl("static$CLICK")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12263", end = 1400),
        @VersionName(name = "field_7790", begin = 1400, end = 2601),
        @VersionName(name = "PICKUP", remap = false, begin = 2601)
    })
    WindowActionType static$CLICK_V900();

    WindowActionType static$SWAP();
    @SpecificImpl("static$SWAP")
    @VersionRange(end = 900)
    default WindowActionType static$SWAP_V_900()
    {
        return FACTORY.create(2);
    }
    @SpecificImpl("static$SWAP")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12265", end = 1400),
        @VersionName(name = "field_7791", begin = 1400, end = 2601),
        @VersionName(name = "SWAP", remap = false, begin = 2601)
    })
    WindowActionType static$SWAP_V900();

    WindowActionType static$PICKUP_ALL();
    @SpecificImpl("static$PICKUP_ALL")
    @VersionRange(end = 900)
    default WindowActionType static$PICKUP_ALL_V_900()
    {
        return FACTORY.create(6);
    }
    @SpecificImpl("static$PICKUP_ALL")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12269", end = 1400),
        @VersionName(name = "field_7793", begin = 1400, end = 2601),
        @VersionName(name = "PICKUP_ALL", remap = false, begin = 2601)
    })
    WindowActionType static$PICKUP_ALL_V900();

    WindowActionType static$SHIFT_CLICK();
    @SpecificImpl("static$SHIFT_CLICK")
    @VersionRange(end = 900)
    default WindowActionType static$SHIFT_CLICK_V_900()
    {
        return FACTORY.create(1);
    }
    @SpecificImpl("static$SHIFT_CLICK")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12264", end = 1400),
        @VersionName(name = "field_7794", begin = 1400, end = 2601),
        @VersionName(name = "QUICK_MOVE", remap = false, begin = 2601)
    })
    WindowActionType static$SHIFT_CLICK_V900();

    WindowActionType static$DROP();
    @SpecificImpl("static$DROP")
    @VersionRange(end = 900)
    default WindowActionType static$DROP_V_900()
    {
        return FACTORY.create(4);
    }
    @SpecificImpl("static$DROP")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12267", end = 1400),
        @VersionName(name = "field_7795", begin = 1400, end = 2601),
        @VersionName(name = "THROW", remap = false, begin = 2601)
    })
    WindowActionType static$DROP_V900();

    WindowActionType static$CLONE();
    @SpecificImpl("static$CLONE")
    @VersionRange(end = 900)
    default WindowActionType static$CLONE_V_900()
    {
        return FACTORY.create(3);
    }
    @SpecificImpl("static$CLONE")
    @VersionRange(begin = 900)
    @WrapMinecraftFieldAccessor({
        @VersionName(name = "field_12266", end = 1400),
        @VersionName(name = "field_7796", begin = 1400, end = 2601),
        @VersionName(name = "CLONE", remap = false, begin = 2601)
    })
    WindowActionType static$CLONE_V900();

    @SpecificImpl("toString")
    default String toString$impl()
    {
        if(this.equals(DRAG))
            return "DRAG";
        else if(this.equals(CLICK))
            return "CLICK";
        else if(this.equals(SWAP))
            return "SWAP";
        else if(this.equals(PICKUP_ALL))
            return "PICKUP_ALL";
        else if(this.equals(SHIFT_CLICK))
            return "SHIFT_CLICK";
        else if(this.equals(DROP))
            return "DROP";
        else if(this.equals(CLONE))
            return "CLONE";
        else
            return "UNKNOWN(" + this.getWrapped() + ")";
    }



    @Deprecated
    static WindowActionType drag()
    {
        return DRAG;
    }
    @Deprecated
    static WindowActionType click()
    {
        return CLICK;
    }
    @Deprecated
    static WindowActionType swap()
    {
        return SWAP;
    }
    @Deprecated
    static WindowActionType pickUpAll()
    {
        return PICKUP_ALL;
    }
    @Deprecated
    static WindowActionType shiftClick()
    {
        return SHIFT_CLICK;
    }
    @Deprecated
    static WindowActionType drop()
    {
        return FACTORY.getStatic().static$DROP();
    }
    @Deprecated
    static WindowActionType copy()
    {
        return CLONE;
    }
}
