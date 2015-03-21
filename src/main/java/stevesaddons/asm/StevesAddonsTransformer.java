package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import stevesaddons.StevesAddons;

import java.util.HashMap;
import java.util.Map;

public class StevesAddonsTransformer implements IClassTransformer, Opcodes
{
    private enum TransformType
    {
        METHOD, FIELD, INNER_CLASS;
    }

    private enum Transformer
    {
        ACTIVATE_TRIGGER("activateTrigger", "(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/components/CommandExecutor", "vswe/stevesfactory/components/CommandExecutorRF");
                    }
                },
        GET_GUI("getGui", "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/interfaces/GuiManager", "stevesaddons/interfaces/GuiRFManager");
                    }
                },
        CREATE_TE("func_149915_a", "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/blocks/TileEntityCluster", "vswe/stevesfactory/blocks/TileEntityRFCluster");
                    }
                },
        MANAGER_INIT("<init>", "()V")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (!(node instanceof LineNumberNode && ((LineNumberNode)node).line == 85) && node != list.getFirst())
                            node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "stevesaddons/asm/StevesHooks", "addCopyButton", "(Lvswe/stevesfactory/blocks/TileEntityManager;)V", false));
                        return list;
                    }
                },
        ITEM_SETTING_LOAD("load", "(Lnet/minecraft/nbt/NBTTagCompound;)V")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (node.getOpcode() != RETURN && node != list.getFirst()) node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(node, new FieldInsnNode(GETFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "stevesaddons/asm/StevesHooks", "fixLoadingStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
                        list.insertBefore(node, new FieldInsnNode(PUTFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        return list;
                    }
                },
        STRING_NULL_CHECK("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        LabelNode labelNode = null;
                        while (node != list.getFirst())
                        {
                            if (node instanceof JumpInsnNode)
                                labelNode = ((JumpInsnNode)node).label;
                            else if (node instanceof VarInsnNode && node.getOpcode() == ALOAD && ((VarInsnNode)node).var == 10)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 10));
                                list.insertBefore(node, new JumpInsnNode(IFNULL, labelNode));
                                break;
                            }
                            node = node.getPrevious();
                        }
                        return list;
                    }
                },
        GET_DESCRIPTION("getDescription", "(Lvswe/stevesfactory/interfaces/GuiManager;)Ljava/lang/String;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == ASTORE)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 0));
                                list.insertBefore(node, new FieldInsnNode(GETFIELD, "vswe/stevesfactory/blocks/ConnectionBlock", "tileEntity", "Lnet/minecraft/tileentity/TileEntity;"));
                                list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "stevesaddons/asm/StevesHooks", "fixToolTip", "(Ljava/lang/String;Lnet/minecraft/tileentity/TileEntity;)Ljava/lang/String;", false));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        ITEM_SEARCH("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode first = list.getFirst();
                        list.insertBefore(first, new VarInsnNode(ALOAD, 0));
                        list.insertBefore(first, new VarInsnNode(ALOAD, 1));
                        list.insertBefore(first, new VarInsnNode(ILOAD, 2));
                        list.insertBefore(first, new MethodInsnNode(INVOKESTATIC, "stevesaddons/asm/StevesHooks", "updateItemSearch", "(Lvswe/stevesfactory/components/ComponentMenuItem;Ljava/lang/String;Z)Ljava/util/List;", false));
                        list.insertBefore(first, new InsnNode(ARETURN));
                        return list;
                    }
                },
        CONTAINER_SEARCH("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        LabelNode label = null;
                        while (node != null)
                        {
                            if (node instanceof JumpInsnNode)
                            {
                                label = ((JumpInsnNode)node).label;
                            }
                            if (node.getOpcode() == ALOAD && ((VarInsnNode)node).var == 8)
                            {
                                list.insertBefore(node, new VarInsnNode(ALOAD, 8));
                                list.insertBefore(node, new VarInsnNode(ALOAD, 1));
                                list.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "stevesaddons/asm/StevesHooks", "containerAdvancedSearch", "(Lvswe/stevesfactory/blocks/ConnectionBlock;Ljava/lang/String;)Z", false));
                                list.insertBefore(node, new JumpInsnNode(IFNE, label));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                },
        GET_PUBLIC_REGISTRATIONS("getRegistrations", "(Lvswe/stevesfactory/blocks/ClusterMethodRegistration;)Ljava/util/List;")
                {
                    @Override
                    public void methodTransform(ClassNode node)
                    {
                        getMethod(node).access = 1;
                    }
                },
        GET_REGISTRATIONS("getRegistrations", "(Lvswe/stevesfactory/blocks/ClusterMethodRegistration;)Ljava/util/List;")
                {
                    @Override
                    public void methodTransform(ClassNode node)
                    {
                        node.methods.remove(getMethod(node));
                    }
                },
        GET_RF_NODE("getTileEntity", "(Ljava/lang/Object;)Lstevesaddons/tileentities/TileEntityRFNode;")
                {
                    @Override
                    public InsnList modifyInstructions(InsnList list)
                    {
                        InsnList result = new InsnList();
                        result.add(new VarInsnNode(ALOAD, 1));
                        result.add(new TypeInsnNode(CHECKCAST, "vswe/stevesfactory/blocks/TileEntityCluster$Pair"));
                        result.add(new FieldInsnNode(GETFIELD, "vswe/stevesfactory/blocks/TileEntityCluster$Pair", "te", "Lvswe/stevesfactory/blocks/TileEntityClusterElement;"));
                        result.add(new TypeInsnNode(CHECKCAST, "stevesaddons/tileentities/TileEntityRFNode"));
                        result.add(new InsnNode(ARETURN));
                        return result;
                    }
                },
        PUBLIC_TE("te", "Lvswe/stevesfactory/blocks/TileEntityClusterElement;", TransformType.FIELD),
        PUBLIC_PAIR("Pair");

        private String name;
        private String args;
        private TransformType type;

        Transformer(String name)
        {
            this(name, "", TransformType.INNER_CLASS);
        }

        Transformer(String name, String args)
        {
            this(name, args, TransformType.METHOD);
        }

        Transformer(String name, String args, TransformType type)
        {
            this.name = name;
            this.args = args;
            this.type = type;
        }

        public InsnList modifyInstructions(InsnList list)
        {
            return list;
        }

        private static InsnList replace(InsnList list, String toReplace, String replace)
        {
            AbstractInsnNode node = list.getFirst();
            InsnList result = new InsnList();
            while (node != null)
            {
                result.add(checkReplace(node, toReplace, replace));
                node = node.getNext();
            }
            return result;
        }

        public String getName()
        {
            return name;
        }

        public String getArgs()
        {
            return args;
        }

        public void methodTransform(ClassNode node)
        {
            MethodNode methodNode = getMethod(node);
            if (node != null)
            {
                methodNode.instructions = modifyInstructions(methodNode.instructions);
                complete();
            }
        }

        public void fieldTransform(ClassNode node)
        {
            FieldNode fieldNode = getField(node);
            if (fieldNode != null)
            {
                fieldNode.access = 1;
                complete();
            }
        }

        public void innerClassTransform(ClassNode node)
        {
            InnerClassNode innerClassNode = getInnerClass(node);
            if (innerClassNode != null)
            {
                innerClassNode.access = 1;
                complete();
            }
        }

        public void transform(ClassNode node)
        {
            switch (this.type)
            {
                case METHOD:
                    methodTransform(node);
                    return;
                case FIELD:
                    fieldTransform(node);
                    return;
                case INNER_CLASS:
                    innerClassTransform(node);
            }
        }

        private static AbstractInsnNode checkReplace(AbstractInsnNode node, String toReplace, String replace)
        {
            if (node instanceof TypeInsnNode && ((TypeInsnNode)node).desc.equals(toReplace))
            {
                return new TypeInsnNode(NEW, replace);
            } else if (node instanceof MethodInsnNode && ((MethodInsnNode)node).owner.contains(toReplace))
            {
                return new MethodInsnNode(node.getOpcode(), replace, ((MethodInsnNode)node).name, ((MethodInsnNode)node).desc, false);
            }
            return node;
        }

        public void complete()
        {
            StevesAddons.log.info("Applied " + this + " transformer");
        }

        public MethodNode getMethod(ClassNode classNode)
        {
            for (MethodNode method : classNode.methods)
            {
                if (method.name.equals(getName()) && method.desc.equals(getArgs()))
                {
                    return method;
                }
            }
            for (MethodNode method : classNode.methods)
            {
                if (method.desc.equals(getArgs()))
                {
                    return method;
                }
            }
            return null;
        }

        public FieldNode getField(ClassNode classNode)
        {
            for (FieldNode field : classNode.fields)
            {
                if (field.name.equals(getName()) && field.desc.equals(getArgs()))
                {
                    return field;
                }
            }
            return null;
        }

        public InnerClassNode getInnerClass(ClassNode classNode)
        {
            String name = classNode.name + "$" + getName();
            for (InnerClassNode inner : classNode.innerClasses)
            {
                if (name.equals(inner.name))
                {
                    return inner;
                }
            }
            return null;
        }
    }

    private enum ClassName
    {
        TE_MANAGER("vswe.stevesfactory.blocks.TileEntityManager", Transformer.ACTIVATE_TRIGGER, Transformer.GET_GUI, Transformer.MANAGER_INIT),
        CLUSTER_BLOCK("vswe.stevesfactory.blocks.BlockCableCluster", Transformer.CREATE_TE),
        ITEM_SETTING_LOAD("vswe.stevesfactory.components.ItemSetting", Transformer.ITEM_SETTING_LOAD),
        COMPONENT_MENU_ITEM("vswe.stevesfactory.components.ComponentMenuItem", Transformer.ITEM_SEARCH),
        CONNECTION_BLOCK("vswe.stevesfactory.blocks.ConnectionBlock", Transformer.GET_DESCRIPTION),
        COMPONENT_MENU_CONTAINER("vswe.stevesfactory.components.ComponentMenuContainer$2", Transformer.CONTAINER_SEARCH),
        CLUSTER_TILE("vswe.stevesfactory.blocks.TileEntityCluster", Transformer.PUBLIC_PAIR, Transformer.GET_PUBLIC_REGISTRATIONS),
        RF_CLUSTER_TILE("vswe.stevesfactory.blocks.TileEntityRFCluster", Transformer.GET_REGISTRATIONS, Transformer.GET_RF_NODE),
        CLUSTER_PAIR("vswe.stevesfactory.blocks.TileEntityCluster$Pair", Transformer.PUBLIC_TE);

        private String name;
        private Transformer[] transformers;

        ClassName(String name, Transformer... transformers)
        {
            this.name = name;
            this.transformers = transformers;
        }

        public String getName()
        {
            return name;
        }

        public Transformer[] getTransformers()
        {
            return transformers;
        }

        public byte[] transform(byte[] bytes)
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

            StevesAddons.log.log(Level.INFO, "Applying Transformer" + (transformers.length > 1 ? "s " : " ") + "to " + getName());

            for (Transformer transformer : getTransformers())
            {
                transformer.transform(classNode);
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
    }

    private static Map<String, ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className : ClassName.values()) classMap.put(className.getName(), className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz != null)
        {
            bytes = clazz.transform(bytes);
            classMap.remove(className);
        }
        return bytes;
    }
}
