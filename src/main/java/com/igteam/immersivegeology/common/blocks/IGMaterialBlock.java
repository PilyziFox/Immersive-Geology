package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IColouredBlock;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.util.BlockstateGenerator;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialBlock extends IGBaseBlock implements IColouredBlock
{
	private Material material;
	private MaterialUseType type;

	public IGMaterialBlock(Material material, MaterialUseType type)
	{
		this(material, type, "");
	}

	public IGMaterialBlock(Material material, MaterialUseType type, String sub)
	{
		super(sub+"block_"+type.getName()+"_"+material.getName(), Properties.create((type.getMaterial()==null?net.minecraft.block.material.Material.ROCK: type.getMaterial())), IGBlockMaterialItem.class, type.getSubGroup());
		this.material = material;
		this.type = type;
		if(itemBlock instanceof IGBlockMaterialItem)
		{
			((IGBlockMaterialItem)itemBlock).material=this.material;
			((IGBlockMaterialItem)itemBlock).subtype=this.type;
		}

		BlockstateGenerator.generateDefaultBlock(material, type);
		ItemJsonGenerator.generateBlockItem(material, type);
	}

	@Override
	public boolean hasCustomBlockColours()
	{
		return true;
	}

	@Override
	public int getRenderColour(BlockState blockState, @Nullable IBlockReader iBlockReader, @Nullable BlockPos blockPos, int i)
	{
		return material.getColor(0);
	}
}
