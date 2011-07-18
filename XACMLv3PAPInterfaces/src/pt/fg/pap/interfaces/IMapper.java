package pt.fg.pap.interfaces;

import java.util.Set;
import pt.fg.pap.operation.OperationResult;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public interface IMapper {
    
    /**
     * May be used to inicialize mapper. Useful to start a connection, for instance.
     * 
     * @return OperationResult
     */
    OperationResult init();

    /**
     * Associates a category to a resource
     *
     * @param category
     * @param resource
     * @return OperationResult
     */
    OperationResult addCategoryToResource(String category, String resource);

    /**
     * Associates a resource to a category
     *
     * @param resource
     * @param category
     * @return
     */
    OperationResult addResourceToCategory(String resource, String category);


    /**
     * Retreives all the resources of a given category
     *
     * @param category
     * @return Set of resource identifiers
     */
    Set<String> getResourcesByCategory(String category);

    /**
     * Retreives all the categories of a given resource
     *
     * @param resource
     * @return Set of category identifiers
     */
    Set<String> getResourceCategories(String resource);

    /**
     * Retreives all available categories
     *
     * @return Set of category identifiers
     */
    Set<String> getAllCategories();
}
