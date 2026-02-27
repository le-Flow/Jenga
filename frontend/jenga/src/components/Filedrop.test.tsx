// @vitest-environment jsdom
import { cleanup, fireEvent, render, screen, waitFor } from "@solidjs/testing-library";
import { ImportResourceService } from "../api";
import { afterEach, describe, expect, test, vi } from "vitest";
import { Filedrop } from "./Filedrop";
import { createTestWrapper } from "../test/testProviders";

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("Filedrop", () => {
    test("shows error if files are uploaded without selected project", async () => {
        const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => undefined);
        const postImportSpy = vi.spyOn(ImportResourceService, "postApiImportGithub").mockResolvedValue();

        render(() => <Filedrop />, {
            wrapper: createTestWrapper({ project: { selectedProject: () => undefined } }),
        });

        const file = new File(['[{"title":"Issue A"}]'], "issues.json", { type: "application/json" });
        await fireEvent.drop(screen.getByTestId("filedrop-zone"), { dataTransfer: { files: [file] } });

        await waitFor(() => {
            expect(postImportSpy).not.toHaveBeenCalled();
            expect(consoleSpy).toHaveBeenCalledWith("No project selected for import");
            expect(screen.getByText("errors.noProjectSelectedForImport")).toBeInTheDocument();
        });
    });

    test("imports files and refetches tickets", async () => {
        const refetchTickets = vi.fn().mockResolvedValue(undefined);
        const postImportSpy = vi.spyOn(ImportResourceService, "postApiImportGithub").mockResolvedValue();

        render(() => <Filedrop />, {
            wrapper: createTestWrapper({
                project: {
                    selectedProject: () => ({ identifier: "project-1" }),
                    refetchTickets,
                },
            }),
        });

        const fileA = new File(['[{"title":"Issue A"}]'], "a.json", { type: "application/json" });
        const fileB = new File(['[{"title":"Issue B"}]'], "b.json", { type: "application/json" });
        await fireEvent.drop(screen.getByTestId("filedrop-zone"), { dataTransfer: { files: [fileA, fileB] } });

        await waitFor(() => {
            expect(postImportSpy).toHaveBeenCalledTimes(2);
            expect(postImportSpy).toHaveBeenNthCalledWith(1, "project-1", [{ title: "Issue A" }]);
            expect(postImportSpy).toHaveBeenNthCalledWith(2, "project-1", [{ title: "Issue B" }]);
            expect(refetchTickets).toHaveBeenCalledTimes(1);
        });
    });

    test("shows error when import API fails", async () => {
        const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => undefined);
        const refetchTickets = vi.fn();
        vi.spyOn(ImportResourceService, "postApiImportGithub").mockRejectedValue(new Error("network"));

        render(() => <Filedrop />, {
            wrapper: createTestWrapper({
                project: {
                    selectedProject: () => ({ identifier: "project-1" }),
                    refetchTickets,
                },
            }),
        });

        const file = new File(['[{"title":"Issue A"}]'], "issues.json", { type: "application/json" });
        await fireEvent.drop(screen.getByTestId("filedrop-zone"), { dataTransfer: { files: [file] } });

        await waitFor(() => {
            expect(consoleSpy).toHaveBeenCalledWith("Failed to import issues", expect.any(Error));
            expect(screen.getByText("errors.failedImportIssues")).toBeInTheDocument();
            expect(refetchTickets).not.toHaveBeenCalled();
        });
    });
});
