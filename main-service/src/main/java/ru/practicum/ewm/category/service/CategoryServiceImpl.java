package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.CanNotDeleteObjectException;
import ru.practicum.ewm.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new ObjectAlreadyExistsException("Category with name=" + category.getName() + " already exists");
        }
        categoryRepository.save(category);
        log.info("Category with id={} and name={} has been created", category.getId(), category.getName());
        return category;
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        checkCategoryForPresenceById(id);
        if (!eventRepository.findAllByCategoryId(id).isEmpty()) {
            throw new CanNotDeleteObjectException("Can not delete category with id=" + id + ", due to category is not empty");
        }
        categoryRepository.deleteById(id);
        log.info("Category with id={} has been deleted", id);
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        checkCategoryForPresenceById(category.getId());
        if (categoryRepository.findByNameAndIdNot(category.getName(), category.getId()).isPresent()) {
            throw new ObjectAlreadyExistsException("Category with name=" + category.getName() + " already exists");
        }
        categoryRepository.saveAndFlush(category);
        log.info("Category with id={} and name={} has been updated", category.getId(), category.getName());
        return category;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("Category with id=" + id + " was not found");
        }
    }

    private void checkCategoryForPresenceById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new ObjectNotFoundException("Category with id=" + id + " was not found");
        }
    }
}