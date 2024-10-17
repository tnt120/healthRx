package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.raport.GenerateReportRequest;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final StatisticsService statisticsService;
    private final DrugsService drugsService;
    private final FriendshipService friendshipService;
    private final UserService userService;
    private final Supplier<User> principalSupplier;
    private final TemplateEngine templateEngine;

    public ReportServiceImpl(
            StatisticsService statisticsService, FriendshipService friendshipService,
            UserService userService, Supplier<User> principalSupplier,
            DrugsService drugsService) {
        this.statisticsService = statisticsService;
        this.friendshipService = friendshipService;
        this.principalSupplier = principalSupplier;
        this.drugsService = drugsService;
        this.userService = userService;

        this.templateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public void generateReport(GenerateReportRequest req, HttpServletResponse response) {
        User user = checkRole();

        checkPermissions(user, req);

        User patient = userService.getUser(req.getUserId());

        Map<String, Object> data = collectData(req, patient);

        generatePdf(data, response, patient);
    }

    private User checkRole() {
        User user = principalSupplier.get();

        if (!user.getRole().equals(Role.DOCTOR)) {
            throw USER_NOT_PERMITTED.getError();
        }

        return user;
    }

    private void checkPermissions(User doctor, GenerateReportRequest req) {
        Friendship friendship = friendshipService.getFriendshipByUsers(doctor.getId(), req.getUserId());

        if (!friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw USER_NOT_PERMITTED.getError();
        }

        if (req.getParametersStats() && !friendship.getParametersAccess()) {
            throw NOT_PARAMS_STATS_PERMISSION.getError();
        }

        if ((req.getDrugsStats() || req.getUserDrugs()) && !friendship.getUserMedicineAccess()) {
            throw NOT_DRUGS_STATS_PERMISSION.getError();
        }

        if (req.getActivitiesStats() && !friendship.getActivitiesAccess()) {
            throw NOT_ACTIVITIES_STATS_PERMISSION.getError();
        }
    }

    private Map<String, Object> collectData(GenerateReportRequest req, User user) {
        Map<String, Object> data = new HashMap<>();

        String userId = req.getUserId();
        LocalDateTime startDate = req.getStartDate();
        LocalDateTime endDate = req.getEndDate();

        if (req.getParametersStats()) {
            data.put("parameters",
                    statisticsService.generateParameterStats(userId, startDate, endDate).stream()
                            .filter(stat -> stat.getFirstLogDate() != null)
                            .toList()
            );
        }

        if (req.getDrugsStats()) {
            data.put("drugs",
                    statisticsService.generateDrugStats(userId, startDate, endDate).stream()
                            .filter(stat -> stat.getFirstLogDate() != null)
                            .toList()
            );
        }

        if (req.getActivitiesStats()) {
            data.put("activities", statisticsService.generateActivityStats(userId, startDate, endDate));
        }

        if (req.getUserDrugs()) {
            data.put("userDrugs", drugsService.getAllUserDrugs(userId));
        }

        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("from", startDate);
        data.put("to", endDate);

        return data;
    }

    @SneakyThrows
    private void generatePdf(Map<String, Object> data, HttpServletResponse response, User user) {
        Context context = new Context();
        context.setVariables(data);

        String htmlContent = templateEngine.process("REPORT", context);

        String fileName = "raport-" + user.getFirstName() + user.getLastName() + '-' + LocalDate.now().toString().substring(0, 10) + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        OutputStream outputStream = response.getOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.flush();
        outputStream.close();
    }
}
