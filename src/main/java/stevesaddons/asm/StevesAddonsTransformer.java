package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StevesAddonsTransformer implements IClassTransformer
{
    private enum MethodName
    {
        ACTIVATE_TRIGGER("activateTrigger","(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V"),
        GET_GUI("getGui","(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;"),
        CE_INIT("<init>","(Lvswe/stevesfactory/blocks/TileEntityManager;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V");

        private String deObf;
        private String obf;
        private String args;

        MethodName(String name, String args)
        {
            deObf = name;
            this.args = args;
        }

        public String getName()
        {
            if (obf==null || !LoadingPlugin.runtimeDeobfEnabled) return deObf;
            return obf;
        }

        public String getArgs()
        {
            return args;
        }
    }

    private enum ClassName
    {

        TE_MANAGER("vswe.stevesfactory.blocks.TileEntityManager",MethodName.ACTIVATE_TRIGGER);
        private String deObf;
        private String obf;
        private MethodName[] methods;


        ClassName(String name, MethodName... methods)
        {
            deObf = name;
            this.methods = methods;
        }

        public String getName()
        {
            if (obf==null || !LoadingPlugin.runtimeDeobfEnabled) return deObf;
            return obf;
        }

        public MethodName[] getMethods()
        {
            return methods;
        }
    }

    private static Map<String,ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className: ClassName.values()) classMap.put(className.getName(),className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz!=null)
        {
            switch (clazz)
            {
                case TE_MANAGER:
                    bytes = replace(MethodName.ACTIVATE_TRIGGER, bytes, "vswe/stevesfactory/components/CommandExecutor", "vswe/stevesfactory/components/CommandExecutorRF");
                    bytes = replace(MethodName.GET_GUI,bytes,"vswe/stevesfactory/interfaces/GuiManager","stevesaddons/interfaces/GuiRFManager");
                    break;
            }
            classMap.remove(className);
        }

        return bytes;
    }

    private byte[] replace(MethodName methodName, byte[] data, String toReplace, String newVal)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        MethodNode methodNode = getMethodByName(classNode, methodName);
        AbstractInsnNode node = methodNode.instructions.getFirst();
        do
        {
            if (node instanceof TypeInsnNode && ((TypeInsnNode)node).desc.equals(toReplace))
            {
                TypeInsnNode newNode = new TypeInsnNode(Opcodes.NEW,newVal);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            } else if (node instanceof MethodInsnNode && ((MethodInsnNode)node).owner.contains(toReplace))
            {
                MethodInsnNode newNode = new MethodInsnNode(node.getOpcode(), newVal, ((MethodInsnNode)node).name,((MethodInsnNode)node).desc,false);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            }
            node = node.getNext();
        }while (node.getNext()!=null);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static AbstractInsnNode findMethodNode(MethodName name, MethodNode methodNode)
    {
        for (Iterator<AbstractInsnNode> itr = methodNode.instructions.iterator(); itr.hasNext();)
        {
            AbstractInsnNode node = itr.next();
            if (node instanceof MethodInsnNode)
            {
                if (((MethodInsnNode)node).name.equals(name.getName())) return node;
            }
        }
        return methodNode.instructions.getLast();
    }

    public static MethodNode getMethodByName(ClassNode classNode, MethodName obfName) {
        List<MethodNode> methods = classNode.methods;
        for (int k = 0; k < methods.size(); k++) {
            MethodNode method = methods.get(k);
            if (method.name.equals(obfName.getName()) && method.desc.equals(obfName.getArgs())) {
                return method;
            }
        }
        return classNode.methods.get(0);
    }
}
