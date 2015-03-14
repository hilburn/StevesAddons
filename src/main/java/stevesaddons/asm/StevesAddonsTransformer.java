package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import stevesaddons.StevesAddons;

import java.util.HashMap;
import java.util.Map;

public class StevesAddonsTransformer implements IClassTransformer
{
    private enum MethodName
    {
        ACTIVATE_TRIGGER("activateTrigger", "(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/components/CommandExecutor", "vswe/stevesfactory/components/CommandExecutorRF");
                    }
                },
        GET_GUI("getGui", "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/interfaces/GuiManager", "stevesaddons/interfaces/GuiRFManager");
                    }
                },
        CREATE_TE("func_149915_a", "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        return replace(list, "vswe/stevesfactory/blocks/TileEntityCluster", "vswe/stevesfactory/blocks/TileEntityRFCluster");
                    }
                },
        MANAGER_INIT("<init>", "()")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (!(node instanceof LineNumberNode && ((LineNumberNode)node).line == 85) && node != list.getFirst())
                            node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                        list.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "addCopyButton", "(Lvswe/stevesfactory/blocks/TileEntityManager;)V", false));
                        return list;
                    }
                },
        ITEM_SETTING_LOAD("load", "(Lnet/minecraft/nbt/NBTTagCompound;)V")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (node.getOpcode() != Opcodes.RETURN && node != list.getFirst()) node = node.getPrevious();
                        list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                        list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                        list.insertBefore(node, new FieldInsnNode(Opcodes.GETFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        list.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "fixLoadingStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
                        list.insertBefore(node, new FieldInsnNode(Opcodes.PUTFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
                        return list;
                    }
                },
        STRING_NULL_CHECK("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        LabelNode labelNode = null;
                        while (node != list.getFirst())
                        {
                            if (node instanceof JumpInsnNode)
                                labelNode = ((JumpInsnNode)node).label;
                            else if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var == 10)
                            {
                                list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 10));
                                list.insertBefore(node, new JumpInsnNode(Opcodes.IFNULL, labelNode));
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
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node.getOpcode() == Opcodes.ASTORE)
                            {
                                list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                                list.insertBefore(node, new FieldInsnNode(Opcodes.GETFIELD, "vswe/stevesfactory/blocks/ConnectionBlock", "tileEntity", "Lnet/minecraft/tileentity/TileEntity;"));
                                list.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "fixToolTip", "(Ljava/lang/String;Lnet/minecraft/tileentity/TileEntity;)Ljava/lang/String;", false));
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
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode first = list.getFirst();
                        list.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 0));
                        list.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 1));
                        list.insertBefore(first, new VarInsnNode(Opcodes.ILOAD, 2));
                        list.insertBefore(first, new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "updateItemSearch", "(Lvswe/stevesfactory/components/ComponentMenuItem;Ljava/lang/String;Z)Ljava/util/List;", false));
                        list.insertBefore(first, new InsnNode(Opcodes.ARETURN));
                        return list;
                    }
                },
        CONTAINER_SEARCH("updateSearch","(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    public InsnList transform(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        LabelNode label = null;
                        while (node != null)
                        {
                            if (node instanceof JumpInsnNode)
                            {
                                label = ((JumpInsnNode)node).label;
                            }
                            if (node.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)node).var==8)
                            {
                                list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 8));
                                list.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 1));
                                list.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "containerAdvancedSearch", "(Lvswe/stevesfactory/blocks/ConnectionBlock;Ljava/lang/String;)Z", false));
                                list.insertBefore(node, new JumpInsnNode(Opcodes.IFNE, label));
                                break;
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                };

        private String name;
        private String args;

        MethodName(String name, String args)
        {
            this.name = name;
            this.args = args;
        }

        public abstract InsnList transform(InsnList list);

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

        private static AbstractInsnNode checkReplace(AbstractInsnNode node, String toReplace, String replace)
        {
            if (node instanceof TypeInsnNode && ((TypeInsnNode)node).desc.equals(toReplace))
            {
                return new TypeInsnNode(Opcodes.NEW, replace);
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
    }

    private enum ClassName
    {

        TE_MANAGER("vswe.stevesfactory.blocks.TileEntityManager", MethodName.ACTIVATE_TRIGGER, MethodName.GET_GUI, MethodName.MANAGER_INIT),
        RF_CLUSTER("vswe.stevesfactory.blocks.BlockCableCluster", MethodName.CREATE_TE),
        ITEM_SETTING_LOAD("vswe.stevesfactory.components.ItemSetting", MethodName.ITEM_SETTING_LOAD),
        COMPONENT_MENU_ITEM("vswe.stevesfactory.components.ComponentMenuItem", MethodName.ITEM_SEARCH),
        CONNECTION_BLOCK("vswe.stevesfactory.blocks.ConnectionBlock", MethodName.GET_DESCRIPTION),
        COMPONENT_MENU_CONTAINER("vswe.stevesfactory.components.ComponentMenuContainer$2", MethodName.CONTAINER_SEARCH);

        private String name;
        private MethodName[] methods;


        ClassName(String name, MethodName... methods)
        {
            this.name = name;
            this.methods = methods;
        }

        public String getName()
        {
            return name;
        }

        public MethodName[] getMethods()
        {
            return methods;
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
            for (MethodName method : clazz.getMethods())
            {
                bytes = transform(method, bytes);
                method.complete();
            }
            classMap.remove(className);
        }

        return bytes;
    }

    private byte[] transform(MethodName methodName, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        MethodNode methodNode = getMethodByName(classNode, methodName);
        methodNode.instructions = methodName.transform(methodNode.instructions);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode getMethodByName(ClassNode classNode, MethodName obfName)
    {
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(obfName.getName()) && method.desc.equals(obfName.getArgs()))
            {
                return method;
            }
        }
        return classNode.methods.get(0);
    }
}
