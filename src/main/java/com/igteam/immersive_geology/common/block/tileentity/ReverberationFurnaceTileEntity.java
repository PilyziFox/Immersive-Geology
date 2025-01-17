package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends PoweredMultiblockTileEntity<ReverberationFurnaceTileEntity, ReverberationRecipe> implements IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IBlockBounds {
    private static final Set<BlockPos> redStonePos = ImmutableSet.of(
            new BlockPos(1, 0, 0)
    );
    private static final BlockPos gasOutputs = new BlockPos(1, 12, 1);
    public static HashMap<Item, Integer> fuelMap = new HashMap<>();
    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);
    public int FUEL_SLOT1 = 0, FUEL_SLOT2 = 1;
    public int OUTPUT_SLOT1 = 2, OUTPUT_SLOT2 = 3;
    public int INPUT_SLOT1 = 4, INPUT_SLOT2 = 5;
    protected FluidTank gasTank;
    protected NonNullList<ItemStack> inventory;
    private Logger log = ImmersiveGeology.getNewLogger();
    private int burntime[] = new int[2];
    private int maxBurntime = 100;

    public ReverberationFurnaceTileEntity() {
        super(ReverberationFurnaceMultiblock.INSTANCE, 0, true, IGTileTypes.REV_FURNACE.get());
        this.inventory = NonNullList.withSize(6, ItemStack.EMPTY);
        burntime[0] = 0;
        burntime[1] = 0;
        gasTank = new FluidTank(1000);
    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();
        if (bX < 3) {
            if (bY == 2) {
                if (bZ == 0) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.25, 1.0, 0.5, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 0.5, 1.0));
                }
                if (bZ == 2 || bZ == 3) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 0.5, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));

                }
                if (bZ == 5) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 0.5, 0.75));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.75));

                }
            }
            if (bY < 2) {
                if (bZ == 0) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.25, 1.0, 1.0, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0));

                }
                if (bZ == 5) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 1.0, 0.75));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75));
                }

                if (bX == 0) {
                    if (bY == 0 && (bZ == 1 || bZ == 4)) {
                        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 1.0, 1.0));
                }

            }
        }
        if (bY >= 6 && bY < 10) {
            if (bX == 3) {
                return Arrays.asList(new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
            if (bX == 5) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
            }
            if (bX == 4) {
                if (bZ == 5 || bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                }
                if (bZ == 0 || bZ == 3) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));

                }
            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    public boolean canUseGui(PlayerEntity player) {
        return this.formed;
    }

    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return (IEBlockInterfaces.IInteractionObjectIE) this.master();
    }

    @Override
    public VoxelShape getBlockBounds(ISelectionContext ctx) {
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    @Nullable
    @Override
    protected ReverberationRecipe getRecipeForId(ResourceLocation id) {
        return ReverberationRecipe.recipes.get(id);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return null;
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return redStonePos;
    }

    @Override
    public void tick() {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;

        checkForNeedlessTicking();

        if (world.isRemote || isDummy())
            return;

        super.tick();


        boolean update = false;

        for (int offset = 0; offset < 2; offset++) {
            if (!isDummy()) {
                //ask me not, but input binds are screwed
                if (master.isBurning(FUEL_SLOT1 + offset)) {
                    master.burntime[offset] = master.burntime[offset] - 1;
                } else if (hasFuel(FUEL_SLOT1 + 3*offset)) {
                    master.burntime[offset] += fuelMap.get(master.getInventory().get(FUEL_SLOT1 + 3*offset).getItem());
                    master.getInventory().get(FUEL_SLOT1 + 3*offset).shrink(1);
                }
            }

            ItemStack inputItem = master.inventory.get(INPUT_SLOT1 + offset);
            if (!inputItem.isEmpty() && inputItem.getCount() > 0) {

                ReverberationRecipe recipe = ReverberationRecipe.findRecipe(inputItem);
                if (recipe != null) {
                    recipe.setSlotOffset(offset);
                    MultiblockProcessInMachine<ReverberationRecipe> process = new MultiblockProcessInMachine<ReverberationRecipe>(recipe, INPUT_SLOT1 + offset);
                    process.setInputAmounts(recipe.input.getCount());

                    if (master.addProcessToQueue(process, true, false)) {
                        update = master.addProcessToQueue(process, false, false);
                    }
                }
            }
        }
        if (update) {
            this.markDirty();
            this.markContainingBlockForUpdate(null);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 6);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("inventory", Utils.writeInventory(inventory));
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[]{gasTank};
    }

    @Nullable
    @Override
    public ReverberationRecipe findRecipeForInsertion(ItemStack itemStack) {
        return ReverberationRecipe.findRecipe(itemStack);
    }

    @Nullable
    @Override
    public int[] getOutputSlots() {
        return new int[]{OUTPUT_SLOT1, OUTPUT_SLOT2};
    }

    @Nullable
    @Override
    public int[] getOutputTanks() {
        return new int[]{0};
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess multiblockProcess) {
        if (multiblockProcess.recipe instanceof ReverberationRecipe) {
            ReverberationRecipe r = (ReverberationRecipe) multiblockProcess.recipe;
            //WTF? -> (processQueue.get(r.getSlotOffset()).recipe.getId().equals(multiblockProcess.recipe.getId()) &&
            return isBurning(r.getSlotOffset());
        }
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess multiblockProcess) {
        if (multiblockProcess.recipe instanceof ReverberationRecipe) {
            ReverberationRecipe r = (ReverberationRecipe) multiblockProcess.recipe;
            int slotOffset = r.getSlotOffset();
            ReverberationFurnaceTileEntity master = this.master();

            if (gasTank.getFluidAmount() < gasTank.getCapacity()) {
                gasTank.fill(new FluidStack(IGRegistrationHolder.getFluidByMaterial(FluidEnum.SulfurDioxide.getMaterial(), false), Math.round(50 * r.getWasteMultipler())), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public int getMaxProcessPerTick() {
        return 2;
    }

    @Override
    public int getProcessQueueMaxLength() {
        int size = 0;
        if ((!master().inventory.get(INPUT_SLOT1).isEmpty())) {
            size++;
        }
        if ((!master().inventory.get(INPUT_SLOT2).isEmpty())) {
            size++;
        }
        return size;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess multiblockProcess) {
        return 1;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    public boolean isBurning(int slot) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return master.burntime[slot] > 0;
    }

    public boolean hasFuel(int slot) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return fuelMap.containsKey(master.inventory.get(slot).getItem());
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        return new IFluidTank[]{gasTank};
    }

    @Override
    protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int i, Direction side) {
        return gasOutputs.equals(posInMultiblock) && (side == null || side == getFacing().getOpposite()); //TODO this seems to always be true? for some reason? ~Muddykat
    }

    @Override
    public int[] getCurrentProcessesStep() {
        return new int[0];
    }

    @Override
    public int[] getCurrentProcessesMax() {
        return new int[0];
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.REV_FURNACE.get();
    }

    @Nullable
    @Override
    public ITextComponent[] getOverlayText(PlayerEntity playerEntity, RayTraceResult rayTraceResult, boolean b) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();

        ArrayList<StringTextComponent> info = new ArrayList<>();
        for (int offset = 0; offset < 2; offset++) {
            String FuelName = master.getInventory().get(FUEL_SLOT1 + offset).getDisplayName().getString();
            String InputName = master.getInventory().get(INPUT_SLOT1 + offset).getDisplayName().getString();
            String OutputName = master.getInventory().get(OUTPUT_SLOT1 + offset).getDisplayName().getString();

            StringTextComponent FuelNames = new StringTextComponent("Fuel Slot[" + offset + "]: " + FuelName + " x" + master.getInventory().get(FUEL_SLOT1 + offset).getCount());
            StringTextComponent InputNames = new StringTextComponent("Input Slot[" + offset + "]: " + InputName + " x" + master.getInventory().get(INPUT_SLOT1 + offset).getCount());
            StringTextComponent OutputNames = new StringTextComponent("Output Slot[" + offset + "]: " + OutputName + " x" + master.getInventory().get(OUTPUT_SLOT1 + offset).getCount());

            info.add(FuelNames);
            info.add(InputNames);
            info.add(OutputNames);
        }

        String TankOutputName = master.getInternalTanks()[0].getFluid().getDisplayName().getString();

        info.add(new StringTextComponent("Gas Output: " + TankOutputName + " x" + master.getInternalTanks()[0].getFluidAmount()));
        info.add(new StringTextComponent("Burn Time[1]: " + master.burntime[FUEL_SLOT1]));
        info.add(new StringTextComponent("Burn Time[2]: " + master.burntime[FUEL_SLOT2]));

        return info.toArray(new ITextComponent[info.size()]);
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }
/*
    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        if(master != null) {
            ItemStack output1SlotStack = master.inventory.get(OUTPUT_SLOT1);
            ItemStack input1SlotStack = master.inventory.get(INPUT_SLOT1);
            ItemStack fuel1SlotStack = master.inventory.get(FUEL_SLOT1);
            ItemStack output2SlotStack = master.inventory.get(OUTPUT_SLOT2);
            ItemStack input2SlotStack = master.inventory.get(INPUT_SLOT2);
            ItemStack fuel2SlotStack = master.inventory.get(FUEL_SLOT2);

            if(output1SlotStack.isItemEqual(itemStack)){
                if(itemStack.getCount() < 64) {
                    int growMax = 64 - Math.min(itemStack.getCount() + output1SlotStack.getCount(), 64);
                    int growAmount = Math.min(growMax, output1SlotStack.getCount());
                    output1SlotStack.shrink(growAmount);
                    itemStack.grow(growAmount);
                    return true;
                }
            }
            if(output2SlotStack.isItemEqual(itemStack)){
                if(itemStack.getCount() < 64) {
                    int growMax = 64 - Math.min(itemStack.getCount() + output2SlotStack.getCount(), 64);
                    int growAmount = Math.min(growMax, output2SlotStack.getCount());
                    output2SlotStack.shrink(growAmount);
                    itemStack.grow(growAmount);
                    return true;
                }
            }

            if(itemStack.isEmpty()){
                if(!output1SlotStack.isEmpty()){
                    playerEntity.setHeldItem(hand, output1SlotStack);
                    master.inventory.set(OUTPUT_SLOT1, ItemStack.EMPTY);
                    return true;
                }
                if(!output2SlotStack.isEmpty()){
                    playerEntity.setHeldItem(hand, output2SlotStack);
                    master.inventory.set(OUTPUT_SLOT2, ItemStack.EMPTY);
                    return true;
                }
                if(!input1SlotStack.isEmpty()) {
                    playerEntity.setHeldItem(hand, input1SlotStack);
                    master.inventory.set(INPUT_SLOT1, ItemStack.EMPTY);
                    return true;
                }
                if(!input2SlotStack.isEmpty()) {
                    playerEntity.setHeldItem(hand, input2SlotStack);
                    master.inventory.set(INPUT_SLOT2, ItemStack.EMPTY);
                    return true;
                }
                if(!fuel1SlotStack.isEmpty()) {
                    playerEntity.setHeldItem(hand, fuel1SlotStack);
                    master.inventory.set(FUEL_SLOT1, ItemStack.EMPTY);
                    return true;
                }
                if(!fuel2SlotStack.isEmpty()) {
                    playerEntity.setHeldItem(hand, fuel2SlotStack);
                    master.inventory.set(FUEL_SLOT2, ItemStack.EMPTY);
                    return true;
                }
            }

            if(master.fuelMap.containsKey(itemStack.getItem())){
                if(fuel1SlotStack.isEmpty()){
                    master.inventory.set(FUEL_SLOT1, itemStack);
                    playerEntity.setHeldItem(hand, ItemStack.EMPTY);
                    return true;
                }
                if(fuel1SlotStack.isItemEqual(itemStack)){
                    if(fuel1SlotStack.getCount() < 64) {
                        int growMax = 64 - Math.min(itemStack.getCount() + fuel1SlotStack.getCount(), 64);
                        int growAmount = Math.min(growMax, itemStack.getCount());
                        fuel1SlotStack.grow(growAmount);
                        itemStack.shrink(growAmount);
                        return true;
                    }
                }
                if(fuel2SlotStack.isEmpty()){
                    master.inventory.set(FUEL_SLOT2, itemStack);
                    playerEntity.setHeldItem(hand, ItemStack.EMPTY);
                    return true;
                }
                if(fuel2SlotStack.isItemEqual(itemStack)){
                    if(fuel2SlotStack.getCount() < 64) {
                        int growMax = 64 - Math.min(itemStack.getCount() + fuel2SlotStack.getCount(), 64);
                        int growAmount = Math.min(growMax, itemStack.getCount());
                        fuel2SlotStack.grow(growAmount);
                        itemStack.shrink(growAmount);
                        return true;
                    }
                }
            }

            if(input1SlotStack.isEmpty()){
                master.inventory.set(INPUT_SLOT1, itemStack);
                playerEntity.setHeldItem(hand, ItemStack.EMPTY);
                return true;
            }
            if(input1SlotStack.isItemEqual(itemStack)){
                if(input1SlotStack.getCount() < 64) {
                    int growMax = 64 - Math.min(itemStack.getCount() + input1SlotStack.getCount(), 64);
                    int growAmount = Math.min(growMax, itemStack.getCount());
                    input1SlotStack.grow(growAmount);
                    itemStack.shrink(growAmount);
                    return true;
                }
            }
            if(input2SlotStack.isEmpty()){
                master.inventory.set(INPUT_SLOT2, itemStack);
                playerEntity.setHeldItem(hand, ItemStack.EMPTY);
                return true;
            }
            if(input2SlotStack.isItemEqual(itemStack)){
                if(input2SlotStack.getCount() < 64) {
                    int growMax = 64 - Math.min(itemStack.getCount() + input2SlotStack.getCount(), 64);
                    int growAmount = Math.min(growMax, itemStack.getCount());
                    input2SlotStack.grow(growAmount);
                    itemStack.shrink(growAmount);
                    return true;
                }
            }
        }

        return false;
    }*/
}