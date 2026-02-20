import { createContext, JSXElement, onCleanup, onMount, useContext } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { LayoutContext } from "./LayoutProvider";

import "shepherd.js/dist/css/shepherd.css";
import Shepherd from "shepherd.js";

type GuideContextType = {
    startGuide: () => void;
    stopGuide: () => void;
};

interface GuideProviderProps {
    children: JSXElement;
}

export const GuideContext = createContext<GuideContextType>();

export const GuideProvider = (props: GuideProviderProps) => {
    const navigate = useNavigate();
    const lCtx = useContext(LayoutContext);
    const tour = new Shepherd.Tour({
        defaultStepOptions: {
            classes: "shepherd-theme-arrows",
            scrollTo: true,
            cancelIcon: {
                enabled: true,
            },
            buttons: [
                {
                    text: "Back",
                    action: () => tour.back(),
                },
                {
                    text: "Next",
                    action: () => tour.next(),
                },
            ],
        },
        useModalOverlay: true,
    });

    const waitForElement = async (selector: string, timeoutMs = 2000) => {
        const startedAt = Date.now();

        while (!document.querySelector(selector)) {
            if (Date.now() - startedAt > timeoutMs) {
                throw new Error(`Guide target not found: ${selector}`);
            }
            await new Promise<void>((resolve) => requestAnimationFrame(() => resolve()));
        }
    };

    const goToPathAndWaitFor = async (path: string, selector: string, timeoutMs = 2000) => {
        navigate(path);
        await waitForElement(selector, timeoutMs);
    };

    const ensureSidebarVisible = async () => {
        lCtx?.setSidebarOpen(true);
    };

    const steps: Shepherd.Step.StepOptions[] = [
        {
            id: "welcome",
            title: "Welcome to Jenga",
            text: "This quick tour follows the typical flow: choose a project, import or create tickets, plan work in Sprint, then update ticket details.",
        },
        {
            id: "navigation",
            title: "Main Navigation",
            text: "Use this menu button to open or close the sidebar. The guide will switch between Home and Sprint automatically.",
            attachTo: {
                element: "#guide-nav-toggle",
                on: "bottom",
            },
            beforeShowPromise: () => waitForElement("#guide-nav-toggle"),
        },
        {
            id: "sidebar",
            title: "App Sections",
            text: "Navigate to Home for project setup and to Sprint for daily ticket planning and status updates.",
            attachTo: {
                element: "#guide-sidebar",
                on: "right",
            },
            beforeShowPromise: async () => {
                await ensureSidebarVisible();
                await waitForElement("#guide-sidebar");
            },
        },
        {
            id: "projects",
            title: "Projects",
            text: "Start here: create or select a project. In Jenga, tickets and labels are always scoped to the selected project.",
            attachTo: {
                element: "#guide-projects",
                on: "bottom",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/", "#guide-projects"),
        },
        {
            id: "file-import",
            title: "File Import",
            text: "You can seed a project by importing GitHub issues as JSON via drag and drop or the file picker.",
            attachTo: {
                element: "#guide-file-import",
                on: "top",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/", "#guide-file-import"),
        },
        {
            id: "ticket-filter",
            title: "Ticket Filter",
            text: "Before planning, narrow the ticket set by title, assignee, status, labels, or all fields.",
            attachTo: {
                element: "#guide-ticket-filter",
                on: "bottom",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/Sprint", "#guide-ticket-filter"),
        },
        {
            id: "kanban",
            title: "Kanban",
            text: "Use drag and drop to move tickets across status columns and assignees directly on the board.",
            attachTo: {
                element: "#guide-kanban",
                on: "top",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/Sprint", "#guide-kanban"),
        },
        {
            id: "backlog",
            title: "Backlog",
            text: "The backlog lists all filtered tickets and lets you create new tickets with the plus button.",
            attachTo: {
                element: "#guide-backlog",
                on: "top",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/Sprint", "#guide-backlog"),
        },
        {
            id: "ticket-details",
            title: "Ticket Details",
            text: "Select any ticket from Kanban or Backlog, edit fields here, then save to persist project changes.",
            attachTo: {
                element: "#guide-ticket-details",
                on: "left",
            },
            beforeShowPromise: () => goToPathAndWaitFor("/Sprint", "#guide-ticket-details"),
        },
        {
            id: "done",
            title: "You're Ready",
            text: "You now know the core Jenga workflow. Pick a project and start planning your sprint.",
            beforeShowPromise: () => goToPathAndWaitFor("/Sprint", "#guide-ticket-details"),
            buttons: [
                {
                    text: "Back",
                    action: () => tour.back(),
                },
                {
                    text: "Finish",
                    action: () => tour.complete(),
                },
            ],
        },
    ];

    onMount(() => {
        tour.addSteps(steps);
    });


    const startGuide = () => {
        if (tour.isActive()) {
            tour.cancel();
        }

        if (tour.steps.length === 0) {
            return;
        }
        tour.start();
    };

    const stopGuide = () => {
        tour.cancel();
    };

    onCleanup(() => {
        tour.cancel();
    });

    const value: GuideContextType = {
        startGuide,
        stopGuide,
    };

    return <GuideContext.Provider value={value}>{props.children}</GuideContext.Provider>;
};
