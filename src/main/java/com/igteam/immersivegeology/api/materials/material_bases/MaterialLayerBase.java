package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

/**
 * @author Pabilo8
 * @since 22.08.2020
 */
public abstract class MaterialLayerBase extends Material
{
	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		switch(useType)
		{
			//items
			case LAYER:
				return true;
		}
		return false;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.WOOD;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.WOOD;
	}
}