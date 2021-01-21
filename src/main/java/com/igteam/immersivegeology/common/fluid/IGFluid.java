package com.igteam.immersivegeology.common.fluid;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class IGFluid extends FlowingFluid {

    protected String name;
    protected Material matType;
    private FluidAttributes attributes;
    private FluidAttributes.Builder builder;

    public IGFluid(MaterialUseType type, Material mat) {
        super();
        builder = FluidAttributes.builder(new ResourceLocation(ImmersiveGeology.MODID, type.getName() + "/" + mat.getFluidPrefix() + "_still"), new ResourceLocation(ImmersiveGeology.MODID, type.getName() + "/"+mat.getFluidPrefix()+"_flowing"));
        builder.color(mat.getColor(mat.getMeltingPoint()));
        builder.density(1000);
        builder.rarity(mat.getRarity());
        builder.temperature(mat.getMeltingPoint());
        builder.viscosity(1000);

        attributes = builder.build(this);
        this.matType = mat;
    }

    @Override
    public Fluid getFlowingFluid() {
        return matType.getFluid(true);
    }

    @Override
    public Fluid getStillFluid() {
        return matType.getFluid(false);
    }

    @Override
    public Item getFilledBucket() {
        return IGRegistryGrabber.grabItemBucket(matType);
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
        BlockPos blockpos = p_204522_2_.up();
        if (p_204522_1_.getBlockState(blockpos).isAir() && !p_204522_1_.getBlockState(blockpos).isOpaqueCube(p_204522_1_, blockpos)) {
            if (p_204522_4_.nextInt(100) == 0) {
                double d0 = (double) ((float) p_204522_2_.getX() + p_204522_4_.nextFloat());
                double d1 = (double) (p_204522_2_.getY() + 1);
                double d2 = (double) ((float) p_204522_2_.getZ() + p_204522_4_.nextFloat());
                p_204522_1_.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                p_204522_1_.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
            }

            if (p_204522_4_.nextInt(200) == 0) {
                p_204522_1_.playSound((double) p_204522_2_.getX(), (double) p_204522_2_.getY(), (double) p_204522_2_.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_204522_4_.nextFloat() * 0.2F, 0.9F + p_204522_4_.nextFloat() * 0.15F, false);
            }
        }

    }

    public void randomTick(World p_207186_1_, BlockPos p_207186_2_, IFluidState p_207186_3_, Random p_207186_4_) {
        if (p_207186_1_.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            int i = p_207186_4_.nextInt(3);
            if (i > 0) {
                BlockPos blockpos = p_207186_2_;

                for (int j = 0; j < i; ++j) {
                    blockpos = blockpos.add(p_207186_4_.nextInt(3) - 1, 1, p_207186_4_.nextInt(3) - 1);
                    if (!p_207186_1_.isBlockPresent(blockpos)) {
                        return;
                    }

                    BlockState blockstate = p_207186_1_.getBlockState(blockpos);
                    if (blockstate.isAir()) {
                        if (this.isSurroundingBlockFlammable(p_207186_1_, blockpos)) {
                            p_207186_1_.setBlockState(blockpos, ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos, p_207186_2_, Blocks.FIRE.getDefaultState()));
                            return;
                        }
                    } else if (blockstate.getMaterial().blocksMovement()) {
                        return;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPos blockpos1 = p_207186_2_.add(p_207186_4_.nextInt(3) - 1, 0, p_207186_4_.nextInt(3) - 1);
                    if (!p_207186_1_.isBlockPresent(blockpos1)) {
                        return;
                    }

                    if (p_207186_1_.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(p_207186_1_, blockpos1)) {
                        p_207186_1_.setBlockState(blockpos1.up(), ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos1.up(), p_207186_2_, Blocks.FIRE.getDefaultState()));
                    }
                }
            }
        }

    }

    private boolean isSurroundingBlockFlammable(IWorldReader p_176369_1_, BlockPos p_176369_2_) {
        Direction[] var3 = Direction.values();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
            if (this.getCanBlockBurn(p_176369_1_, p_176369_2_.offset(direction))) {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(IWorldReader p_176368_1_, BlockPos p_176368_2_) {
        return p_176368_2_.getY() >= 0 && p_176368_2_.getY() < 256 && !p_176368_1_.isBlockLoaded(p_176368_2_) ? false : p_176368_1_.getBlockState(p_176368_2_).getMaterial().isFlammable();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IParticleData getDripParticleData() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    protected void beforeReplacingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, BlockState p_205580_3_) {
        this.triggerEffects(p_205580_1_, p_205580_2_);
    }

    public int getSlopeFindDistance(IWorldReader p_185698_1_) {
        return p_185698_1_.getDimension().doesWaterVaporize() ? 4 : 2;
    }

    public BlockState getBlockState(IFluidState p_204527_1_) {
        return (BlockState) IGRegistryGrabber.grabFluidBlock(MaterialUseType.FLUID_BLOCKS, matType).getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(p_204527_1_));
    }

    public boolean isEquivalentTo(Fluid p_207187_1_) {
        return p_207187_1_ == matType.getFluid(false) || p_207187_1_ == matType.getFluid(true);
    }

    public int getLevelDecreasePerBlock(IWorldReader p_204528_1_) {
        return p_204528_1_.getDimension().doesWaterVaporize() ? 1 : 2;
    }

    public boolean func_215665_a(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
        return p_215665_1_.func_215679_a(p_215665_2_, p_215665_3_) >= 0.44444445F && p_215665_4_.isIn(FluidTags.WATER);
    }

    public int getTickRate(IWorldReader p_205569_1_) {
        return p_205569_1_.getDimension().isNether() ? 10 : 30;
    }

    public int func_215667_a(World p_215667_1_, BlockPos p_215667_2_, IFluidState p_215667_3_, IFluidState p_215667_4_) {
        int i = this.getTickRate(p_215667_1_);
        if (!p_215667_3_.isEmpty() && !p_215667_4_.isEmpty() && !(Boolean) p_215667_3_.get(FALLING) && !(Boolean) p_215667_4_.get(FALLING) && p_215667_4_.func_215679_a(p_215667_1_, p_215667_2_) > p_215667_3_.func_215679_a(p_215667_1_, p_215667_2_) && p_215667_1_.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    private void triggerEffects(IWorld p_205581_1_, BlockPos p_205581_2_) {
        p_205581_1_.playEvent(1501, p_205581_2_, 0);
    }

    protected boolean canSourcesMultiply() {
        return false;
    }

    protected void flowInto(IWorld p_205574_1_, BlockPos p_205574_2_, BlockState p_205574_3_, Direction p_205574_4_, IFluidState p_205574_5_) {
        if (p_205574_4_ == Direction.DOWN) {
            IFluidState ifluidstate = p_205574_1_.getFluidState(p_205574_2_);
            if (ifluidstate.isTagged(FluidTags.WATER)) {
                if (p_205574_3_.getBlock() instanceof FlowingFluidBlock) {
                    p_205574_1_.setBlockState(p_205574_2_, ForgeEventFactory.fireFluidPlaceBlockEvent(p_205574_1_, p_205574_2_, p_205574_2_, Blocks.STONE.getDefaultState()), 3);
                }

                this.triggerEffects(p_205574_1_, p_205574_2_);
                return;
            }
        }

        super.flowInto(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_4_, p_205574_5_);
    }

    protected boolean ticksRandomly() {
        return true;
    }

    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected FluidAttributes createAttributes() {
        return attributes;
    }

    public String getName() {
        return this.name;
    }

    //extends the abstract IGFluid to allow source and flowing variants
    public static class Flowing extends IGFluid {
        public Flowing(MaterialUseType type, Material mat) {
            super(type, mat);
            this.name = "fluid_flowing_"+mat.getName();
            setRegistryName(ImmersiveGeology.MODID, this.name);
        }

        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
            super.fillStateContainer(p_207184_1_);
            p_207184_1_.add(new IProperty[]{LEVEL_1_8});
        }

        public int getLevel(IFluidState p_207192_1_) {
            return (Integer)p_207192_1_.get(LEVEL_1_8);
        }

        public boolean isSource(IFluidState p_207193_1_) {
            return false;
        }
    }

    public static class Source extends IGFluid {
        public Source(MaterialUseType type, Material mat) {
            super(type, mat);
            this.name = "fluid_source_"+mat.getName();
            setRegistryName(ImmersiveGeology.MODID, this.name);
        }

        public int getLevel(IFluidState p_207192_1_) {
            return 8;
        }

        public boolean isSource(IFluidState p_207193_1_) {
            return true;
        }
    }
}