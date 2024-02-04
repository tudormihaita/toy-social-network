package application.toysocialnetwork.repository.file;

import application.toysocialnetwork.domain.Entity;
import application.toysocialnetwork.exceptions.InvalidIdentifierException;
import application.toysocialnetwork.repository.memory.AbstractMemoryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends AbstractMemoryRepository<ID, E> {

    private final Path filePath;

    public AbstractFileRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        loadData();
    }

    private void loadData() {
        try {
            Files.readAllLines(filePath).forEach(line ->
                    super.save(extractEntity(List.of(line.split(",")))));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData() {
        try {
            List<CharSequence> stringList = new ArrayList<>();
            super.findAll().forEach(entity -> stringList.add(entityAsString(entity)));
            Files.write(filePath, String.join("\n",stringList).getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract E extractEntity(List<String> attributes);
    protected abstract String entityAsString(E entity);

    @Override
    public Optional<E> save(E entity) throws InvalidIdentifierException {
        Optional<E> unsavedEntity =  super.save(entity);
        if(unsavedEntity.isEmpty()) {
            writeData();
        }

        return unsavedEntity;
    }

    @Override
    public Optional<E> delete(ID id) throws InvalidIdentifierException {
        Optional<E> entity = super.delete(id);
        if(entity.isPresent()) {
            writeData();
        }

        return entity;
    }

    @Override
    public Optional<E> update(E entity) throws InvalidIdentifierException {
        Optional<E> oldEntity = super.update(entity);
        if(oldEntity.isPresent()) {
            writeData();
        }

        return oldEntity;
    }
}
