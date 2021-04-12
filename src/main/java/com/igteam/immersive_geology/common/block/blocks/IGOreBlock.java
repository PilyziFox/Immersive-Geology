package com.igteam.immersive_geology.common.block.blocks;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.client.render.RenderLayerHandler;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.layer.Layer;

import java.util.ArrayList;
import java.util.List;

public class IGOreBlock extends BlockBase implements IEBlockInterfaces.IColouredBlock {

    protected MaterialStoneBase.EnumStoneType blockStoneType;

    protected Material[] ore_materials;

    public IGOreBlock(String registryName, Material[] material, MaterialUseType useType) {
        super(registryName, material[0], useType);

        blockStoneType = material[0].getStoneType();
        this.ore_materials = material;
        RenderLayerHandler.setRenderType(this, RenderLayerHandler.RenderTypeSkeleton.TRANSLUCENT);
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
        List<ItemStack> dropList = new ArrayList<>();
        dropList.add(new ItemStack(IGRegistrationHolder.getItemByMaterial(ore_materials[0], ore_materials[1], MaterialUseType.ORE_CHUNK, blockStoneType)));
        return dropList;
    }

    public Material getStoneBase(){
        return ore_materials[0];
    }

    public Material getOreBase(){
        return ore_materials[1];
    }

    @Override
    public boolean hasCustomBlockColours() {
        return true;
    }

    @Override
    public int getRenderColour(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos, int pass)
    {
        return ore_materials[MathHelper.clamp(pass,0,ore_materials.length-1)].getColor(1);//cheating to get white color for vanilla stone to render nicely and have colored items
    }
}
