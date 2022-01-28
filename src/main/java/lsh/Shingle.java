package lsh;

import java.util.Arrays;

public class Shingle {

    private final int Size;
    private int[] Data;
    private int SetCount = 0;

    public Shingle(int size) {
        Size = size;
        Data = new int[Size];
    }

    public int get(int index) {
        return Data[index];
    }

    public int[] get() {
        return Data;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(Data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != obj.getClass()) {
            return false;
        }
        Shingle s = (Shingle) obj;
        if (s.Size != Size) {
            return false;
        } else {
            for (int i = 0; i < Size; i++) {
                if (s.Data[i] != Data[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public void add(int value) {
        if (SetCount < Size) {
            Data[SetCount++] = value;
        }
    }

    public boolean isFull() {
        return SetCount == Size;
    }

    public Shingle addRolling(int value) {
        Shingle el = new Shingle(Size);
        for (int i = 1; i < Size; i++) {
            el.add(Data[i]);
        }
        el.add(value);
        return el;
    }
}
