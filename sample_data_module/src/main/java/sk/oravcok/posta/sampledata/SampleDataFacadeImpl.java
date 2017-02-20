package sk.oravcok.posta.sampledata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.oravcok.posta.entity.Employee;
import sk.oravcok.posta.entity.Job;
import sk.oravcok.posta.entity.Place;
import sk.oravcok.posta.enums.PlaceType;
import sk.oravcok.posta.service.EmployeeService;
import sk.oravcok.posta.service.JobService;
import sk.oravcok.posta.service.PlaceService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * implementation of SampleDataFacade
 *
 * @author Ondrej Oravcok
 * @version 25-Dec-16.
 */
@Component
@Transactional
public class SampleDataFacadeImpl implements SampleDataFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataFacadeImpl.class);

    private final Map<String, Place> places = new HashMap<>();
    private final Map<String, Employee> employees = new HashMap<>();
    private final Map<String, Job> jobs = new HashMap<>();

    @Inject
    private PlaceService placeService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private JobService jobService;

    @Override
    public void loadData() {
        initPlaces();
        initEmployees();
        initJobs();
        LOGGER.info("sample data loaded");
    }

    private void initPlaces() {
        places.put("Window 1", place("Priehradka c.1", PlaceType.WINDOW, null));
        places.put("Window 2", place("Priehradka c.2", PlaceType.WINDOW, null));
        places.put("Window 3", place("Priehradka c.3", PlaceType.WINDOW, null));
        places.put("Window 4", place("Priehradka c.4", PlaceType.WINDOW, null));
        places.put("Window 5", place("Priehradka c.5", PlaceType.WINDOW, null));
        places.put("Window 6", place("Priehradka c.6", PlaceType.WINDOW, null));
        places.put("Window 7", place("Priehradka c.7", PlaceType.WINDOW, null));
        places.put("Window 8", place("Priehradka c.8", PlaceType.WINDOW, null));
        places.put("Background", place("Zazemie", PlaceType.BACKGROUND, "kontroly, uzavierky"));
        places.put("Sorting", place("Triedenie", PlaceType.BACKGROUND, null));
        LOGGER.info("places loaded");
    }

    private void initEmployees() {
        employees.put("web", employee("Mark", "Webber", null, "mark.webber@porsche.de",
                date(27, 8, 1976), "+421 950 436 723", "Borovnianska 43, 96001 Zvolen"));
        employees.put("vet", employee("Sebastian", "Vettel", null, "seb@ferrari.it",
                date(3, 7, 1987), "+421 950 434 423", "Kukucinova 3, 96001 Zvolen"));
        employees.put("ros", employee("Nico", "Rosberg", null, "nico.rosberg@mercedes.de",
                date(27, 6, 1985), "+421 950 535 723", "Slatinska cesta 102/55, 96250 Ocova"));
        employees.put("ric", employee("Daniel", "Ricciardo", "Ing.", "daniel.ricciardo@redbullracing.com",
                date(1, 7, 1989), "+421 950 676 390", "Gagarinova 72, 93400 Ocova"));
        employees.put("ves", employee("Max", "Verstappen", null, "max33verstappen@redbullracing.com",
                date(30, 9, 1997), "+421 950 448 011","Nam. SNP 60, 92007 Kokava nad Rimavicou"));
        employees.put("hul", employee("Nico", "Hulkenberg", null, "hulk@saharafi.co.uk",
                date(19, 8, 1987), "+421 917 834 447", "Milady Horackovej 1023/16, 79030 Podhorie"));
        employees.put("mas", employee("Felipe", "Massa", "Mr.", "felipemassa@williams.co.uk",
                date(25, 4, 1981), "+421 908 334 813", "Cintorinska 17, 82204 Velke Uherce"));
        employees.put("but", employee("Jenson", "Button", null, "pushthebutton@mclaren.co.uk",
                date(19, 1, 1980), "+421 949 446 600", "J. C. Hronskeho 66, 82202 Zvolenska Slatina"));
        employees.put("msc", employee("Michael", "Schumacher", null, "keepfighting@schumi.de",
                date(3, 1, 1969), "+421 902 234 198", "Hurth, 82202 Germany"));
        employees.put("mks", employee("Mick", "Schumacher", null, "futurechampion@schumi.de",
                date(22, 3, 1999), "+421 918 231 014", "Zurich, 348SW Switzerland"));
        LOGGER.info("employees loaded");
    }

    private void initJobs() {
        jobs.put("mon-web-win1", job(employees.get("web"), places.get("Window 1"), date(2, 1, 2017), time("07:10"), time("12:30")));
        jobs.put("mon-web-win1_2", job(employees.get("web"), places.get("Window 1"), date(2, 1, 2017), time("12:50"), time("13:20")));
        jobs.put("mon-vet-win1", job(employees.get("vet"), places.get("Window 1"), date(2, 1, 2017), time("13:20"), time("16:10")));
        jobs.put("mon-vet-win1_2", job(employees.get("vet"), places.get("Window 1"), date(2, 1, 2017), time("16:30"), time("18:10")));
        jobs.put("tue-web-win1", job(employees.get("web"), places.get("Window 1"), date(3, 1, 2017), time("07:10"), time("12:40")));
        jobs.put("tue-vet-win1", job(employees.get("vet"), places.get("Window 1"), date(3, 1, 2017), time("13:15"), time("18:00")));
        jobs.put("wed-web-win1", job(employees.get("web"), places.get("Window 1"), date(4, 1, 2017), time("07:00"), time("09:50")));
        jobs.put("wed-web-win1_2", job(employees.get("web"), places.get("Window 1"), date(4, 1, 2017), time("10:00"), time("12:50")));
        jobs.put("wed-vet-win1", job(employees.get("vet"), places.get("Window 1"), date(4, 1, 2017), time("13:10"), time("18:10")));
        jobs.put("wed-vet-win1_2", job(employees.get("vet"), places.get("Window 1"), date(4, 1, 2017), time("18:30"), time("19:10")));
        jobs.put("thu-web-win1", job(employees.get("web"), places.get("Window 1"), date(5, 1, 2017), time("07:30"), time("12:40")));
        jobs.put("thu-vet-win1", job(employees.get("vet"), places.get("Window 1"), date(5, 1, 2017), time("13:15"), time("18:00")));
        jobs.put("fri-web-win1", job(employees.get("web"), places.get("Window 1"), date(6, 1, 2017), time("07:10"), time("12:40")));
        jobs.put("fri-vet-win1", job(employees.get("vet"), places.get("Window 1"), date(6, 1, 2017), time("13:15"), time("18:00")));

        jobs.put("mon-ros-win2", job(employees.get("ros"), places.get("Window 2"), date(2, 1, 2017), time("07:40"), time("14:35")));
        jobs.put("mon-ric-win2", job(employees.get("ric"), places.get("Window 2"), date(2, 1, 2017), time("14:20"), time("17:55")));
        jobs.put("tue-ros-win2", job(employees.get("ros"), places.get("Window 2"), date(3, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("tue-ric-win2", job(employees.get("ric"), places.get("Window 2"), date(3, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("wed-ros-win2", job(employees.get("ros"), places.get("Window 2"), date(4, 1, 2017), time("07:40"), time("14:35")));
        jobs.put("wed-ric-win2", job(employees.get("ric"), places.get("Window 2"), date(4, 1, 2017), time("14:20"), time("17:55")));
        jobs.put("thu-ros-win2", job(employees.get("ros"), places.get("Window 2"), date(5, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("thu-ric-win2", job(employees.get("ric"), places.get("Window 2"), date(5, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("fri-ros-win2", job(employees.get("ros"), places.get("Window 2"), date(6, 1, 2017), time("07:40"), time("14:35")));
        jobs.put("fri-ric-win2", job(employees.get("ric"), places.get("Window 2"), date(6, 1, 2017), time("14:20"), time("17:55")));

        jobs.put("mon-ves-win3", job(employees.get("ves"), places.get("Window 3"), date(2, 1, 2017), time("07:40"), time("14:35")));
        jobs.put("mon-hul-win3", job(employees.get("hul"), places.get("Window 3"), date(2, 1, 2017), time("14:20"), time("17:55")));
        jobs.put("tue-mas-win3", job(employees.get("mas"), places.get("Window 3"), date(3, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("tue-ves-win3", job(employees.get("ves"), places.get("Window 3"), date(3, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("wed-hul-win3", job(employees.get("hul"), places.get("Window 3"), date(4, 1, 2017), time("07:35"), time("13:25")));
        jobs.put("wed-mas-win3", job(employees.get("mas"), places.get("Window 3"), date(4, 1, 2017), time("13:35"), time("17:30")));
        jobs.put("thu-ves-win3", job(employees.get("ves"), places.get("Window 3"), date(5, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("thu-hul-win3", job(employees.get("hul"), places.get("Window 3"), date(5, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("fri-mas-win3", job(employees.get("mas"), places.get("Window 3"), date(6, 1, 2017), time("07:35"), time("13:25")));
        jobs.put("fri-ves-win3", job(employees.get("ves"), places.get("Window 3"), date(6, 1, 2017), time("13:25"), time("17:30")));

        jobs.put("mon-hul-win4", job(employees.get("hul"), places.get("Window 4"), date(2, 1, 2017), time("07:40"), time("14:10")));
        jobs.put("mon-mas-win4", job(employees.get("mas"), places.get("Window 4"), date(2, 1, 2017), time("14:20"), time("17:55")));
        jobs.put("tue-but-win4", job(employees.get("but"), places.get("Window 4"), date(3, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("tue-hul-win4", job(employees.get("hul"), places.get("Window 4"), date(3, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("wed-mas-win4", job(employees.get("mas"), places.get("Window 4"), date(4, 1, 2017), time("07:35"), time("13:20")));
        jobs.put("wed-but-win4", job(employees.get("but"), places.get("Window 4"), date(4, 1, 2017), time("13:25"), time("17:30")));
        jobs.put("thu-hul-win4", job(employees.get("hul"), places.get("Window 4"), date(5, 1, 2017), time("13:55"), time("18:10")));
        jobs.put("thu-mks-win4", job(employees.get("mks"), places.get("Window 4"), date(5, 1, 2017), time("07:20"), time("12:55")));
        jobs.put("fri-but-win4", job(employees.get("but"), places.get("Window 4"), date(6, 1, 2017), time("07:35"), time("13:25")));
        jobs.put("fri-mas-win4", job(employees.get("mas"), places.get("Window 4"), date(6, 1, 2017), time("13:40"), time("17:00")));

        LOGGER.info("jobs loaded");
    }

    private Place place(String name, PlaceType type, String annotation) {
        Place place = new Place();

        place.setName(name);
        place.setPlaceType(type);
        place.setAnnotation(annotation);

        placeService.create(place);
        return place;
    }

    private Employee employee(String name, String surname, String title, String email, LocalDate birth, String phone, String address) {
        Employee employee = new Employee();

        employee.setName(name);
        employee.setSurname(surname);
        employee.setTitle(title);
        employee.setBirth(birth);
        employee.setEmail(email);
        employee.setAddress(address);
        employee.setPhone(phone);

        employeeService.create(employee);
        return employee;
    }

    private Job job(Employee employee, Place place, LocalDate jobDate, LocalTime jobStart, LocalTime jobEnd) {
        Job job = new Job();

        job.setEmployee(employee);
        job.setPlace(place);
        job.setJobDate(jobDate);
        job.setJobStart(jobStart);
        job.setJobEnd(jobEnd);

        jobService.create(job);
        return job;
    }

    private LocalDate date(int day, int month, int year) {
        return LocalDate.of(year, month, day);
    }

    private LocalTime time(String time) {
        return LocalTime.parse(time);
    }

}
