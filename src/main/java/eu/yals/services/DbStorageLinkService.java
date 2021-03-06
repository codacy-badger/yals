package eu.yals.services;

import eu.yals.models.Link;
import eu.yals.models.dao.LinkRepo;
import eu.yals.result.GetResult;
import eu.yals.result.StoreResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Service, which interacts with database
 *
 * @since 2.0
 */
@Slf4j
@Qualifier("dbStorage")
@Component
public class DbStorageLinkService implements LinkService {

    private final LinkRepo repo;

    public DbStorageLinkService(LinkRepo repo) {
        this.repo = repo;
    }

    @Override
    public GetResult getLink(String ident) {
        Optional<Link> result;
        try {
            result = repo.findSingleByIdent(ident);
        } catch (DataAccessResourceFailureException e) {
            return new GetResult.DatabaseDown().withException(e);
        } catch (Exception e) {
            return new GetResult.Fail().withException(e);
        }

        return result.<GetResult>map(link -> new GetResult.Success(link.getLink()))
                .orElseGet(GetResult.NotFound::new);
    }

    @Override
    public StoreResult storeNew(String ident, String link) {
        Link linkObject = Link.create(ident, link);
        try {
            repo.save(linkObject);
            return new StoreResult.Success();
        } catch (DataAccessResourceFailureException e) {
            return new StoreResult.DatabaseDown().withException(e);
        } catch (Exception e) {
            log.error("Exception on storing new " + Link.class.getSimpleName(), e);
            return new StoreResult.Fail("Failed to add new record");
        }

    }
}
