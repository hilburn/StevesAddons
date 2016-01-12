package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import stevesaddons.StevesAddons;

import java.util.*;

public class StevesAddonsTransformer implements IClassTransformer, Opcodes
{
    private enum TransformType
    {
        METHOD, FIELD, INNER_CLASS, MODIFY, MAKE_PUBLIC, DELETE
    }

    private enum Transformer
    {
        ITEM_SEARCH("updateSearch", "(Ljava/lang/String;Z)Ljava/util/List;")
                {
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
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
        LOAD_DEFAULT("loadDefault", "()V")
                {
                    private Set<String> change = new HashSet<String>(Arrays.asList("largeOpenHitBox", "largeOpenHitBoxMenu", "autoBlacklist", "autoSide"));
                    @Override
                    protected InsnList modifyInstructions(InsnList list)
                    {
                        AbstractInsnNode node = list.getFirst();
                        while (node != null)
                        {
                            if (node instanceof FieldInsnNode && change.contains(((FieldInsnNode)node).name))
                            {
                                list.remove(node.getPrevious());
                                list.insertBefore(node, new InsnNode(ICONST_1));
                            }
                            node = node.getNext();
                        }
                        return list;
                    }
                }
        ;

        protected String name;
        protected String args;
        protected TransformType type;
        protected TransformType action;

        Transformer(String name, String args)
        {
            this(name, args, TransformType.METHOD, TransformType.MODIFY);
        }

        Transformer(String name, String args, TransformType type, TransformType action)
        {
            this.name = name;
            this.args = args;
            this.type = type;
            this.action = action;
        }

        protected InsnList modifyInstructions(InsnList list)
        {
            return list;
        }

        public String getName()
        {
            return name;
        }

        public String getArgs()
        {
            return args;
        }

        protected void methodTransform(ClassNode node)
        {
            MethodNode methodNode = getMethod(node);
            if (methodNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        methodNode.instructions = modifyInstructions(methodNode.instructions);
                        break;
                    case DELETE:
                        node.methods.remove(methodNode);
                        break;
                    case MAKE_PUBLIC:
                        methodNode.access = (methodNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void fieldTransform(ClassNode node)
        {
            FieldNode fieldNode = getField(node);
            if (fieldNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        modifyField(fieldNode);
                        break;
                    case DELETE:
                        node.fields.remove(fieldNode);
                        break;
                    case MAKE_PUBLIC:
                        fieldNode.access = (fieldNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void modifyField(FieldNode fieldNode)
        {
        }


        private void innerClassTransform(ClassNode node)
        {
            InnerClassNode innerClassNode = getInnerClass(node);
            if (innerClassNode != null)
            {
                switch (action)
                {
                    case MODIFY:
                        modifyInnerClass(innerClassNode);
                        break;
                    case DELETE:
                        node.innerClasses.remove(innerClassNode);
                        break;
                    case MAKE_PUBLIC:
                        innerClassNode.access = (innerClassNode.access & ~7) ^ 1;
                }
                complete();
            }
        }

        private void modifyInnerClass(InnerClassNode innerClassNode)
        {
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
        COMPONENT_MENU_ITEM("vswe.stevesfactory.components.ComponentMenuItem", Transformer.ITEM_SEARCH),
        SETTINGS("vswe.stevesfactory.settings.Settings", Transformer.LOAD_DEFAULT);

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
            classReader.accept(classNode, 0);

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
